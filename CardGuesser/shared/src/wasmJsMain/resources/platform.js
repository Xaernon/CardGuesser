// Browser-side helpers for the web version. The Kotlin code calls these through
// `window.cardGuesser` (see Platform.wasmJs.kt).
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
        getItem: function (key) {
            try { return window.localStorage.getItem(key); } catch (e) { return null; }
        },
        setItem: function (key, value) {
            try { window.localStorage.setItem(key, value); } catch (e) { /* private mode */ }
        },
        ready: function () {
            var loading = document.getElementById('loading');
            if (loading) loading.remove();
        }
    };
})();
