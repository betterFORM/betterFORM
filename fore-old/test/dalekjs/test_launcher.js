module.exports = {
    'Page title is correct': function (test) {
        test
            .open('http://localhost:9001/index.html')
            .waitForElement('fore-launcher')
            .assert.title().is('Fore Bootstrap Page')
            .assert.numberOfElements('fore-launcher', 1 , 'found one instance of the betterFORM fore launcher')

            .done();
    }
};