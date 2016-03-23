/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global module: false, grunt: false, process: false */

/*
 * Grunt tasks are run with gulp-grunt in the gulpfile.js
 *
 */
module.exports = function (grunt) {
    'use strict';

    grunt.initConfig({
        w20: {
            optimize: {
                options: {
                    buildConfig: {
                        baseUrl: 'dist/',
                        out: 'dist/hub.min.js'
                    }
                }

            }
        }
    });

    grunt.loadNpmTasks('grunt-w20');

    grunt.registerTask('default', ['w20:optimize'], null);
};
