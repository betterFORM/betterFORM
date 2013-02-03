define(["dojo/_base/declare","dojo/dom","dojo/dom-attr","dojo/dom-construct", "dojo/dom-style","jquery","jplot"],
    function(declare, dom, domAttr, domConstruct, domStyle,$,jplot){
        return declare(null, {
            linechartNode:null,
            dataSource: null,
            timeline:null,

            constructor:function() {
                console.debug("Linechart.constructor: this: ", this);
                this.srcNodeRef = arguments[1];
                this.dataSource = domAttr.get(this.srcNodeRef, "data-bf");
                var origId = domAttr.get(this.srcNodeRef, "id");
                this.id = origId.substring(0,origId.length -5) + "tlcontainer";
                console.debug("Timeline: dataSource:", this.dataSource, " origId: ",origId);

                this.linechartNode = domConstruct.toDom("<div class='xfLinechart' id='"+ this.id+"'></div>");
                domConstruct.place(this.linechartNode, origId, "after");
                domStyle.set(this.srcNodeRef, "display", "none");
                this.initLinechart();
            },

            initLinechart:function() {
                console.debug("initializing LineChart");
                var targetId = this.id;
                require(["dateAxisRenderer","cursor", "highlighter","dojo/ready"], function(dateAxisRenderer,cursor, highlighter,ready){
                    ready(function(){
                        $.jqplot.config.enablePlugins = true;
                        console.debug("$.jqplot.config.enablePlugins: ",$.jqplot.config.enablePlugins);
                        // For these examples, don't show the to image button.
                        $.jqplot._noToImageButton = true;
                        console.debug("$.jqplot._noToImageButton : ",$.jqplot._noToImageButton );

                        console.debug("targetId: ", targetId);
                        var data = [['1/1/1869' , 1], ['1/1/1870' , 0], ['1/1/1871' , 3], ['1/1/1872' , 0], ['1/1/1873' , 0], ['1/1/1874' , 0], ['1/1/1875' , 0], ['1/1/1876' , 0], ['1/1/1877' , 3], ['1/1/1878' , 0], ['1/1/1879' , 0], ['1/1/1880' , 0], ['1/1/1881' , 0], ['1/1/1882' , 0], ['1/1/1883' , 1], ['1/1/1884' , 1], ['1/1/1885' , 0], ['1/1/1886' , 2], ['1/1/1887' , 1], ['1/1/1888' , 0], ['1/1/1889' , 2], ['1/1/1890' , 0], ['1/1/1891' , 0], ['1/1/1892' , 1], ['1/1/1893' , 0], ['1/1/1894' , 2], ['1/1/1895' , 2], ['1/1/1896' , 2], ['1/1/1897' , 6], ['1/1/1898' , 1], ['1/1/1899' , 2], ['1/1/1900' , 0], ['1/1/1901' , 3], ['1/1/1902' , 0], ['1/1/1903' , 1], ['1/1/1904' , 0], ['1/1/1905' , 0], ['1/1/1906' , 1], ['1/1/1907' , 1], ['1/1/1908' , 1], ['1/1/1909' , 5], ['1/1/1910' , 2], ['1/1/1911' , 2], ['1/1/1912' , 0], ['1/1/1913' , 2], ['1/1/1914' , 0], ['1/1/1915' , 0], ['1/1/1916' , 0], ['1/1/1917' , 0], ['1/1/1918' , 0], ['1/1/1919' , 4], ['1/1/1920' , 0], ['1/1/1921' , 3], ['1/1/1922' , 5], ['1/1/1923' , 0], ['1/1/1924' , 0], ['1/1/1925' , 0], ['1/1/1926' , 2], ['1/1/1927' , 1], ['1/1/1928' , 1], ['1/1/1929' , 1], ['1/1/1930' , 1], ['1/1/1931' , 0], ['1/1/1932' , 0], ['1/1/1933' , 7], ['1/1/1934' , 8], ['1/1/1935' , 2], ['1/1/1936' , 4], ['1/1/1937' , 7], ['1/1/1938' , 6], ['1/1/1939' , 6], ['1/1/1940' , 5], ['1/1/1941' , 1], ['1/1/1942' , 2], ['1/1/1943' , 1], ['1/1/1944' , 1], ['1/1/1945' , 0], ['1/1/1946' , 1], ['1/1/1947' , 0], ['1/1/1948' , 1], ['1/1/1949' , 1], ['1/1/1950' , 20], ['1/1/1951' , 30], ['1/1/1952' , 15], ['1/1/1953' , 23], ['1/1/1954' , 13], ['1/1/1955' , 23], ['1/1/1956' , 26], ['1/1/1957' , 40], ['1/1/1958' , 13], ['1/1/1959' , 23], ['1/1/1960' , 23], ['1/1/1961' , 36], ['1/1/1962' , 22], ['1/1/1963' , 22], ['1/1/1964' , 22], ['1/1/1965' , 29], ['1/1/1966' , 19], ['1/1/1967' , 29], ['1/1/1968' , 28], ['1/1/1969' , 38], ['1/1/1970' , 38], ['1/1/1971' , 26], ['1/1/1972' , 35], ['1/1/1973' , 22], ['1/1/1974' , 25], ['1/1/1975' , 24], ['1/1/1976' , 30], ['1/1/1977' , 36], ['1/1/1978' , 29], ['1/1/1979' , 36], ['1/1/1980' , 33], ['1/1/1981' , 26], ['1/1/1982' , 29], ['1/1/1983' , 22], ['1/1/1984' , 23], ['1/1/1985' , 18], ['1/1/1986' , 31], ['1/1/1987' , 22], ['1/1/1988' , 28], ['1/1/1989' , 28], ['1/1/1990' , 47], ['1/1/1991' , 53], ['1/1/1992' , 47], ['1/1/1993' , 6], ['1/1/1994' , 15], ['1/1/1995' , 12], ['1/1/1996' , 12], ['1/1/1997' , 20], ['1/1/1998' , 11], ['1/1/1999' , 6], ['1/1/2000' , 7], ['1/1/2001' , 8], ['1/1/2002' , 2], ['1/1/2003' , 9], ['1/1/2004' , 10], ['1/1/2005' , 10], ['1/1/2006' , 12], ['1/1/2007' , 12], ['1/1/2008' , 16], ['1/1/2009' , 18], ['1/1/2010' , 12], ['1/1/2011' , 14], ['1/1/2012' , 10]];
                        var opts = {
                            title: 'German Books of Law / Year',
                            axes: {
                                xaxis: {
                                    renderer: $.jqplot.DateAxisRenderer,
                                    label: 'Years',
                                    min:'1/1/1869',
                                    max:'1/1/2013',
                                    labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
                                    tickRenderer: $.jqplot.CanvasAxisTickRenderer,
                                    tickOptions: {
                                        labelPosition: 'middle',
                                        angle: -30,
                                        formatString:"%Y"
                                    },
                                    tickInterval: "10 years"
                                },
                                yaxis: {
                                    label: 'Count',
                                    min:0,
                                    labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
                                    tickRenderer: $.jqplot.CanvasAxisTickRenderer,
                                    tickOptions: {
                                        // labelPosition: 'middle',
                                        angle:-30
                                    }
                                }
                            },
                            series:[{lineWidth:4, color:"red", shadowDepth:5, markerOptions:{show:false}}],
                            grid: {
                                drawGridLines: false,
                                gridLineColor: 'lightgray' ,

                                background: 'white',
                                borderColor: 'gray',
                                borderWidth: 2.0,
                                shadow: true,
                                shadowAngle: 45,
                                shadowOffset: 1.5,
                                shadowWidth: 3,
                                shadowDepth: 3,
                                shadowAlpha: 0.07,
                                renderer: $.jqplot.CanvasGridRenderer,
                                rendererOptions: {}
                            },
                            cursor:{
                                zoom:false,
                                looseZoom: false,
                                showTooltip:false,
                                followMouse: true,
                                showTooltipOutsideZoom: true,
                                constrainOutsideZoom: false
                            }
                        };

                        console.debug("opts: ",opts);
                        var plot2 = $.jqplot(targetId, [data], opts);
                        console.debug("created LineChart: plot2",plot2);
                    })
                })

            }
        });
    });