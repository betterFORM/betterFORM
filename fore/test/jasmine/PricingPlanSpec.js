describe('<pricing-plan>', function(){
    beforeEach(function(){
        document.body.innerHTML = __html__['test/jasmine/pricing-plan-fixture.html'];
    });

    describe('defaults', function(){
        beforeEach(function(){
            var it = document.getElementsByTagName('pricing-plan')[0];
        });

        it('should create an element', function() {
            expect(it).toNotBe(undefined);
        });
    });
});