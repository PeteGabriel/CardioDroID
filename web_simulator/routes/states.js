var express = require('express');
var router = express.Router();
var sendState = require('../coms/state-coms');

/* GET home page. */
router.get('/', function(req, res, next) {
    res.render('states2', {
        title: 'CardioWheel Simulator'
    });
});

router.use('/set', function(req, res, next) {
    if (req.query.level) {
        return next();
    }
    //bad request
    res.status(400).send("Bad Request - 'level' parameter is missing.");
});

router.get('/set', function(req, res) {
    // Send new state to BLE.
    sendState(req.query.level)
    res.status(204).end();
});

module.exports = router;
