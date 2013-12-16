module.exports = {
    'Page title is correct': function (test) {
        test
            .open('http://localhost:9001/index.html')
            .waitForElement('#container')
            .assert.title().is('betterFORM', 'It has title')
            .assert.numberOfElements('fore-processor', 1, 'found one instance of the betterFORM fore processor')
            .assert.notVisible('fore-processor','fore-processor is not visible')
            .assert.numberOfElements('fore-model', 1, 'found one model element')
            .assert.numberOfElements('fore-instance', 1, 'found one instance element')
            .assert.exists('.foreProcessor', 'polymer was initialized')
            .assert.text('foo2','hello','created instance node got the value implied by bound input')
            // assert.text does only work in Firefox since Chrome is respecting the ShadowDom and can't see the text
            // .assert.text('h1', 'hi from the fore processor', 'polymer was initialized'
            .done();
    }
};