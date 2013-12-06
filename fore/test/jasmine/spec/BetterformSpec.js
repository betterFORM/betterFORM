describe("Betterform.js Unit Tests", function() {
  it("instantiate betterform", function() {
    var bf = new Betterform();
    expect(bf).toBeDefined();
    expect(bf.events).toBeDefined();
    expect(bf.events.xforms).toBeDefined();
    expect(bf.events.betterform).toBeDefined();


  });
    it("fire some events", function() {
        var bf = new Betterform();
        bf.add("xforms-value-changed");
        bf.add("xforms-focus");
        bf.fireEvents();

    });

});
