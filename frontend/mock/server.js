/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

const dist = process.argv.slice(2)[0];

const express = require('express');
const bodyParser = require("body-parser");
const app = express();
var home = require('./home');
var cards = require('./components-card');

function is(req, mimeType) {
    return req.headers.accept === mimeType;
}

function isHal(req) {
    return is(req, 'application/hal+json');
}

function isJsonHome(req) {
    return is(req, 'application/json-home');
}

function setHalResponse(res) {
    res.set({'content-type': 'application/hal+json'});
}

function sendCards(cards, i, j, res) {
    setHalResponse(res);
    var list = cards.slice(i || 0, j);
    res.send(200, new Buffer(JSON.stringify({
        _embedded: {
            components: list
        }
    })));
}

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

app.get('/', (req, res, next) => {
    return isJsonHome(req) ? res.json(home) : next();
});

app.get('/popular', (req, res, next) => {
    return isHal(req) ? sendCards(cards, 0, 6, res) : next();
});

app.get('/recent', (req, res, next) => {
    return isHal(req) ? sendCards(cards, 5, 11, res) : next();
});

app.get('/components', (req, res) => {
    res.json(cards);
});

app.post('/user/components', (req, res, next) => {
    setTimeout(() => {
        res.json(req.body);
    }, 5000);
});

if (dist === 'dist') {
    app.use(express.static(__dirname + '/../dist'));
} else {
    app.use(express.static(__dirname + '/../.'));
}



app.listen(3000, () => {
    console.log('Hub app listening on port 3000!');
});

