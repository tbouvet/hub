/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
var tests = [];

for (var file in window.__karma__.files) {
    if (/.spec\.js$/.test(file)) {
        tests.push(file);
    }
}

window.w20 = {
    configuration: {
        '/base/bower_components/w20/core/w20-core.w20.json': {
            modules: {
                application: {
                    id: 'w20-test',
                    home: '/test'
                }
            },
            vars: {
                'components-path': '/base/bower_components'
            }
        }
    },
    deps: tests,
    callback: window.__karma__.start
};

requirejs.config({
    paths: {
        '{angular-mocks}': '/base/bower_components/angular-mocks',
        '{hub}': '/base/hub'
    },
    shim: {
        '{angular-mocks}/angular-mocks': [ '{angular}/angular' ]
    }
});

requirejs([ '/base/bower_components/w20/core/modules/w20.js' ]);



