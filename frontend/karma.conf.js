/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global module: false, config: false */
module.exports = function(config) {
    'use strict';

    config.set({
        frameworks: [ 'jasmine', 'requirejs', 'phantomjs-shim' ],
        files: [
            'test-main.js',

            { pattern: 'w20.app.json', served: true, included: false },

            { pattern: 'hub/**/*.js', included: false },
            { pattern: 'hub/**/*.html', included: false },
            { pattern: 'bower_components/**/*', included: false }
        ],
        preprocessors: {
            'hub/modules/*.js': 'coverage'
        },
        reporters: ['dots' ],
        port: 9876,
        colors: true,
        logLevel: 'INFO',
        browsers: [ 'PhantomJS' ]
    });
};