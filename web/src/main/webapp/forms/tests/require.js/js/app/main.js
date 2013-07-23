define(["require", "jQuery", "jQueryUI"], function (req, $)
{
        req(['jQueryUI/datepicker', 'jQueryUI-datepicker-de'],
            function () {
                $('#datepicker').datepicker();
            }
        );
});

