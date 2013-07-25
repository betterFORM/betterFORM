require(['require', 'jquery'], function( req, $ ) {
        req(['jquery.ui.datepicker'],
            function () {
                $('.datepicker').datepicker();
            }
        );
});
