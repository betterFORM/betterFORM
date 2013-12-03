module.exports = {
    'Page title is correct': function (test) {
        test
            .open('http://localhost:9001/index.html')
            .waitForElement('#container')
            .assert.title().is('betterFORM', 'It has title')
            .assert.numberOfElements('fore-processor', 1, 'found one instance of the betterFORM fore processor')
            .assert.exists('.foreProcessor', 'polymer was initialized')
            // assert.text does only work in Firefox since Chrome is respecting the ShadowDom and can't see the text
            // .assert.text('h1', 'hi from the fore processor', 'polymer was initialized'
            .done();
    }
};