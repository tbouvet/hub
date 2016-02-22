/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

const express = require('express');
const app = express();
var home = require('./home');
var cards = require('./components-card');

app.get('/', (req, res, next) => {
    return req.headers.accept === 'application/json-home' ? res.json(home) : next();
});

app.get('/components', (req, res) => {
    res.json(cards);
});

app.get('/popular', (req, res, next) => {
    if (req.headers.accept === 'application/hal+json') {
        res.set({ 'content-type': 'application/hal+json' });

        var popular = cards.slice(0,5);

        res.send(200, new Buffer(JSON.stringify({
            _embedded: {
                components: popular
            }
        })));

    } else {
        next();
    }
});

app.get('/recent', (req, res, next) => {
    if (req.headers.accept === 'application/hal+json') {
        res.set({ 'content-type': 'application/hal+json' });

        var recent = cards.slice(5, 10);

        res.send(200, new Buffer(JSON.stringify({
            _embedded: {
                components: recent
            }
        })));

    } else {
        next();
    }
});

app.use(express.static(__dirname + '/../.'));

app.listen(3000, () => {
    console.log('Hub app listening on port 3000!');
});

