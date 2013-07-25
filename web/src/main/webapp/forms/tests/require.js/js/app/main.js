require(['require', 'jquery', 'polymer'], function( req, $ ) {
        Polymer('tk-element', {
            ready: function() {
                //...
            }
        });
        req(['jquery.ui.datepicker'],
            function () {
                $('#datepicker').datepicker();
            }
        );
});
