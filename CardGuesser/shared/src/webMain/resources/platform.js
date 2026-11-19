// Browser-side helpers for the web version. The Kotlin code calls these through
// `window.cardGuesser` (see Platform.web.kt).

// If the game can't start, say so instead of showing "Loading…" forever.
(function () {
    var started = false;
    window.__cardGuesserStarted = function () { started = true; };
    // These messages show before the game (and its texts) load: pick French from the saved choice
    // or the browser's language.
    var saved = null;
    try { saved = localStorage.getItem('cardguesser.language'); } catch (e) { /* private mode */ }
    var fr = (saved || navigator.language || '').indexOf('fr') === 0;
    var text = fr ? {
        failed: 'Le jeu n’a pas pu démarrer dans ce navigateur.',
        update: 'Mettre à jour le navigateur (ou iOS) règle généralement le problème.',
        slowTitle: 'Chargement en cours…',
        slow: 'La première visite télécharge environ 10 Mo. Avec une connexion lente, cela peut prendre une minute.'
    } : {
        failed: "The game couldn't start in this browser.",
        update: 'Updating the browser (or iOS) usually fixes this.',
        slowTitle: 'Still loading…',
        slow: 'The first visit downloads about 10 MB. On a slow connection this can take a minute.'
    };
    document.addEventListener('DOMContentLoaded', function () {
        var el = document.getElementById('loading');
        if (el && fr && !started && el.textContent === 'Loading…') el.textContent = 'Chargement…';
    });
    function show(title, detail) {
        if (started) return;
        var el = document.getElementById('loading');
        if (!el) return;
        el.innerHTML = '';
        var t = document.createElement('div'); t.textContent = title; el.appendChild(t);
        if (detail) { var d = document.createElement('small'); d.textContent = detail; el.appendChild(d); }
    }
    window.addEventListener('error', function (e) {
        show(text.failed, (e && e.message ? e.message + ' — ' : '') + text.update);
    });
    window.addEventListener('unhandledrejection', function (e) {
        var r = e && e.reason;
        show(text.failed, (r && (r.message || String(r)) ? (r.message || String(r)) + ' — ' : '') + text.update);
    });
    setTimeout(function () {
        show(text.slowTitle, text.slow);
    }, 20000);
})();
(function () {
    // Compose reads the browser's language list to pick the texts; let the game override it.
    // Some browsers (Safari) keep these on the navigator object itself, others on its prototype,
    // so both are covered; the original value is used until a language is chosen in the game.
    ['languages', 'language'].forEach(function (prop) {
        var proto = Object.getOwnPropertyDescriptor(Navigator.prototype, prop);
        var own = Object.getOwnPropertyDescriptor(navigator, prop);
        var fallback = navigator[prop];
        function original(self) {
            if (own && own.get) return own.get.call(self);
            if (own && 'value' in own) return own.value;
            if (proto && proto.get) return proto.get.call(self);
            return fallback;
        }
        var override = {
            configurable: true,
            get: function () {
                var code = window.__cardGuesserLanguage;
                if (!code) return original(this);
                return prop === 'languages' ? [code] : code;
            }
        };
        try { Object.defineProperty(Navigator.prototype, prop, override); } catch (e) { /* not configurable */ }
        try { Object.defineProperty(navigator, prop, override); } catch (e) { /* not configurable */ }
    });
})();

window.cardGuesser = (function () {
    'use strict';

    // iOS reports gravity with the opposite sign to the spec that Android browsers follow.
    var isApple = /iPhone|iPad|iPod|Macintosh/.test(navigator.userAgent);
    var sensorZ = 0;
    var keyZ = 0;
    var audio = null;
    var motionAsked = false;
    var wantAwake = false;
    var wakeLock = null;

    window.addEventListener('devicemotion', function (event) {
        var g = event.accelerationIncludingGravity;
        if (!g || g.z === null || g.z === undefined) return;
        var z = g.z / 9.81;
        sensorZ = isApple ? -z : z;
    });

    // Arrow keys stand in for tilting when there is no motion sensor (laptop demos).
    window.addEventListener('keydown', function (event) {
        if (event.key === 'ArrowDown') keyZ = -1;
        if (event.key === 'ArrowUp') keyZ = 1;
    });
    window.addEventListener('keyup', function (event) {
        if (event.key === 'ArrowDown' || event.key === 'ArrowUp') keyZ = 0;
    });

    // Sound and (on iOS) motion access may only be unlocked from inside a user gesture, so this
    // runs on the first touches anywhere on the page.
    function unlock() {
        try {
            if (!audio) audio = new (window.AudioContext || window.webkitAudioContext)();
            if (audio.state === 'suspended') audio.resume();
        } catch (e) { /* sound is optional */ }
        if (!motionAsked && typeof DeviceMotionEvent !== 'undefined' &&
            typeof DeviceMotionEvent.requestPermission === 'function') {
            motionAsked = true;
            DeviceMotionEvent.requestPermission().catch(function () { motionAsked = false; });
        }
    }
    ['touchend', 'pointerup', 'click', 'keydown'].forEach(function (type) {
        document.addEventListener(type, unlock, true);
    });

    function acquireWakeLock() {
        if (!wantAwake || wakeLock || !navigator.wakeLock) return;
        navigator.wakeLock.request('screen').then(function (lock) {
            wakeLock = lock;
            lock.addEventListener('release', function () { wakeLock = null; });
            if (!wantAwake) lock.release();
        }).catch(function () { /* not allowed right now */ });
    }
    // The browser drops the lock when the tab is hidden; take it again on return.
    document.addEventListener('visibilitychange', function () {
        if (document.visibilityState === 'visible') acquireWakeLock();
    });

    function lockLandscape() {
        try {
            if (screen.orientation && screen.orientation.lock) screen.orientation.lock('landscape').catch(function () {});
        } catch (e) { /* optional */ }
    }

    // On iPhone the only way to lose Safari's bars is to add the game to the home screen; say so once.
    function showHomeScreenTip() {
        var iOS = /iPhone|iPod/.test(navigator.userAgent);
        var standalone = window.navigator.standalone || window.matchMedia('(display-mode: standalone)').matches ||
            window.matchMedia('(display-mode: fullscreen)').matches;
        var seen = false;
        try { seen = localStorage.getItem('cardguesser.tipSeen') === '1'; } catch (e) { /* private mode */ }
        if (!iOS || standalone || seen) return;
        var tip = document.getElementById('tip');
        if (!tip) return;
        var fr = (window.__cardGuesserLanguage || navigator.language || '').indexOf('fr') === 0;
        tip.textContent = fr
            ? 'Astuce : pour jouer en plein écran, touche Partager puis « Sur l’écran d’accueil ».'
            : 'Tip: for full screen, tap Share, then “Add to Home Screen”.';
        tip.style.display = 'block';
        function hide() {
            tip.style.display = 'none';
            try { localStorage.setItem('cardguesser.tipSeen', '1'); } catch (e) { /* private mode */ }
        }
        tip.addEventListener('click', hide);
        setTimeout(hide, 9000);
    }

    return {
        /** +1 when the screen faces the sky, -1 when it faces the floor. */
        tilt: function () {
            return keyZ ? keyZ : sensorZ;
        },
        beep: function (frequency, millis) {
            if (!audio) return;
            try {
                var osc = audio.createOscillator();
                var gain = audio.createGain();
                var end = audio.currentTime + millis / 1000;
                osc.frequency.value = frequency;
                gain.gain.setValueAtTime(0.25, audio.currentTime);
                gain.gain.exponentialRampToValueAtTime(0.001, end);
                osc.connect(gain);
                gain.connect(audio.destination);
                osc.start();
                osc.stop(end);
            } catch (e) { /* sound is optional */ }
        },
        vibrate: function (millis) {
            if (navigator.vibrate) navigator.vibrate(millis);
        },
        keepAwake: function (on) {
            wantAwake = on;
            if (on) acquireWakeLock();
            else if (wakeLock) wakeLock.release();
        },
        /** Renders an emoji with the device's emoji font; returns base64 PNG data. */
        emojiPng: function (emoji, pixels) {
            var canvas = document.createElement('canvas');
            canvas.width = canvas.height = Math.round(pixels * 1.25);
            var ctx = canvas.getContext('2d');
            ctx.font = pixels + 'px "Apple Color Emoji", "Segoe UI Emoji", "Noto Color Emoji", sans-serif';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillText(emoji, canvas.width / 2, canvas.height / 2 + pixels * 0.06);
            return canvas.toDataURL('image/png').split(',')[1];
        },
        // The app's texts follow navigator.language(s); the in-app language choice overrides them.
        setLanguage: function (code) {
            window.__cardGuesserLanguage = code;
        },
        getItem: function (key) {
            try { return window.localStorage.getItem(key); } catch (e) { return null; }
        },
        setItem: function (key, value) {
            try { window.localStorage.setItem(key, value); } catch (e) { /* private mode */ }
        },
        ready: function () {
            if (window.__cardGuesserStarted) window.__cardGuesserStarted();
            var loading = document.getElementById('loading');
            if (loading) loading.remove();
            showHomeScreenTip();
        },
        // Rounds are played sideways: take the whole screen where the browser allows it (Android).
        // iPhone Safari doesn't let pages go full screen; there the home-screen app is the way.
        fullscreen: function (on) {
            try {
                var el = document.documentElement;
                var isFull = document.fullscreenElement || document.webkitFullscreenElement;
                if (on && !isFull && window.matchMedia('(pointer: coarse)').matches) {
                    var req = el.requestFullscreen || el.webkitRequestFullscreen;
                    if (req) {
                        var p = req.call(el, { navigationUI: 'hide' });
                        if (p && p.then) p.then(lockLandscape, function () { /* needs a recent tap; fine */ });
                        else lockLandscape();
                    }
                } else if (on && isFull) {
                    lockLandscape();
                }
                if (!on && screen.orientation && screen.orientation.unlock) screen.orientation.unlock();
            } catch (e) { /* optional */ }
        }
    };
})();
