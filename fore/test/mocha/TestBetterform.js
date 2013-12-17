var assert = require("assert"),
    Betterform = require( '../../app/scripts/Betterform');

describe('Betterform.js Unit Tests in Mocha', function(){
    describe('instatiation', function(){
        it("instantiate betterform", function() {
            var bf = new Betterform();
            expect(bf).toBeDefined();
        });
        it("check betterform childs", function() {
            var bf = new Betterform();
            expect(bf.events).toBeDefined();
            expect(bf.events.xforms).toBeDefined();
            expect(bf.events.betterform).toBeDefined();
        });
    });
    describe("Betterform.js Event Tests", function() {
        it("fire some events", function() {
            var bf = new Betterform();
            bf.add("xforms-value-changed");
            bf.add("xforms-focus");
            bf.fireEvents();
        });
    });
});




