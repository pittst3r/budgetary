var Util = {

  confirm: function(text, successFn) {
    var confirm = window.confirm(text);
    if (confirm) {
      successFn();
    } else {
      return false;
    }
  }

}

var Budget = {

  pathConstructor: function(id) {
    return "/budgets/" + id;
  },

  destroy: function(id, elem) {
    var request = new Request({
          url: Budget.pathConstructor(id),
          method: "delete",
          emulation: false,
          onSuccess: function() {
            elem.getParent("tr").destroy();
          }
        });
    Util.confirm("Delete this budget?", function() {
      request.send();
    });
  },

  domReady: function() {
    $$("a.delete").addEvent("click", function(event) {
      event.stop();
      var id = this.getProperty("data-id");
      Budget.destroy(id, this);
    });
  }

}

window.addEvent('domready', function() {
  Budget.domReady();
});
