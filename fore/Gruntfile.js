'use strict';
var LIVERELOAD_PORT = 35729;
var lrSnippet = require('connect-livereload')({port: LIVERELOAD_PORT});
var mountFolder = function (connect, dir) {
    return connect.static(require('path').resolve(dir));
};

module.exports = function(grunt) {
    // load all grunt tasks
    require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);
    require('time-grunt')(grunt);

    var httpServerPort = 9001;


    var foreConfig = {
        srcDir: 'app',
        webModule: '../web',
        dist: 'dist',
        pagesTarget: '/target/betterform',
        devTarget: '/target/betterform/WEB-INF/classes/META-INF/resources',
        elements: '/elements'
    };

    grunt.initConfig({
        fore: foreConfig,
        webDevTarget: foreConfig.webModule + foreConfig.devTarget,
        webPagesTarget: foreConfig.webModule + foreConfig.pagesTarget,
        elementsTarget: foreConfig.webModule + foreConfig.devTarget + foreConfig.elements,


        //WATCH tasks
        watch: {
            options: {
                nospawn: true,
                livereload: true
            },
            elementsScripts: {
                files: ['<%= fore.srcDir %>/elements/**/*.js'],
                tasks: ['rsync:developmentElements']
            },
            elementsHTML: {
                files: ['<%= fore.srcDir %>/elements/**/*.html'],
                tasks: ['rsync:developmentElements']
            },
            styles: {
                files: ['<%= fore.srcDir %>/styles/*.less'],
                tasks: ['less:development']
            },
            images: {
                files: ['<%= fore.srcDir %>/images/**/*'],
                tasks: ['rsync:developmentImages']
            },
            pages: {
                files: ['<%= fore.srcDir %>/pages/**/*'],
                tasks: ['rsync:developmentPages']
            },
            html: {
                files: ['<%= fore.srcDir %>/*.html']
            },
            target: {
                options: {
                    livereload: true
                },
                files: ['<%= webPagesTarget %>/pages/*.xhtml','<%= elementsTarget %>/*.html']
            }
        },

        //RSYNC tasks
        rsync: {
            options: {
                args: ["-vpc"],
                recursive: true
            },
            developmentScripts: {
                options: {
                    // !!! The last "/" is IMPORTANT here!!!!
                    src: '<%= fore.srcDir %>/bower_components',
                    dest: '<%= webDevTarget %>'
                }
            },
            developmentElements: {
                options: {
                    src: '<%= fore.srcDir %>/elements',
                    dest: '<%= webDevTarget %>'
                }
            },
            developmentImages: {
                options: {
                    src: '<%= fore.srcDir %>/images',
                    dest: '<%= webDevTarget %>'
                }
            },
            developmentStyles: {
                options: {
                    src: '<%= fore.srcDir %>/styles',
                    dest: '<%= webDevTarget %>'
                }
            },
            developmentPages: {
                options: {
                    src: '<%= fore.srcDir %>/pages',
                    dest: '<%= webPagesTarget %>'
                }
            },
            dist: {
                options: {
                    // !!! The last "/" is IMPORTANT here!!!!
                    src: ['<%= fore.dist %>/', '<%= fore.srcDir %>/elements'],
                    dest: '<%= webDevTarget %>',
                    exclude: ['build.html', 'pages']
                }
            }
        },

        //LESS compiler tasks
        less: {
            options: {
                paths: ['<%= fore.srcDir %>/styles']
            },
            // target name
            development: {
                // no need for files, the config below should work
                expand: true,
                cwd: '<%= fore.srcDir %>/styles',
                src:    '*.less',
                ext:    '.css',
                dest: '<%= fore.srcDir %>/styles'
            },

            dist: {
                // no need for files, the config below should work
                expand: true,
                cwd: '<%= fore.srcDir %>/styles',
                src:    '*.less',
                ext:    '.css',
                dest: '<%= fore.dist %>/styles',
                options: {
                    compress:true
                }



            }
        },

        //USEMIN tasks
        useminPrepare: {
            html: ['<%= fore.srcDir %>/*.html'],
            options: {
                dest: '<%= fore.dist %>'
            }
        },

        usemin: {
            html: '<%= fore.dist %>/*.html',
            options: {
                dirs: ['<%= fore.dist %>']
            }
        },

        imagemin: {
            dist: {
                files: [{
                    expand: true,
                    cwd: '<%= fore.srcDir %>/images',
                    src: '{,*/}*.{png,jpg,jpeg}',
                    dest: '<%= fore.dist %>/images'
                }]
            }
        },


        //CLEAN tasks
        clean: {
            dist: ['.tmp', '<%= fore.dist %>/*'],
            webDevTarget: {
                options: {
                    force: true
                },
                src: ['<%= webDevTarget %>/scripts', '<%= webDevTarget %>/elements', '<%= webDevTarget %>/images', '<%= webDevTarget %>/styles' ]
            }
        },

        copy: {
            dist: {
                files: [{
                    expand: true,
                    dot: true,
                    cwd: '<%= fore.srcDir %>',
                    dest: '<%= fore.dist %>',
                    src: [
                        '*.{ico,txt}',
                        '.htaccess',
                        'elements/**',
                        'pages/**'
                    ]
                }]
            },
            html: {
                files: [{
                    expand: true,
                    dot: true,
                    cwd: '<%= fore.srcDir %>',
                    dest: '<%= fore.dist %>',
                    src: [
                        '*.html'
                    ]
                }]
            }

        },


        dalek: {
            options: {
                dalekfile:false,
                browser: ['chrome'],
                reporter: ['html']
            },

            tests: {
                src: ['test/dalekjs/test_launcher.js']
            }
        },
        htmlmin: {
            dist: {
                options: {
                    removeCommentsFromCDATA: true,
                    removeComments:true,
                    // https://github.com/yeoman/grunt-usemin/issues/44
                    collapseWhitespace: true,
                    collapseBooleanAttributes: true,
                    removeAttributeQuotes: true,
                    removeRedundantAttributes: true,
                    useShortDoctype: true,
                    removeEmptyAttributes: true,
                    removeOptionalTags: false
                },
                files: [{
                    expand: true,
                    cwd: '<%= fore.dist %>',
                    src: '*.html',
                    dest: '<%= fore.dist %>'
                }]
            }
        },

        connect: {
            //Run "app" in grunt server
            livereload: {
                options: {
                    port: httpServerPort,
                    base:  'app',
                    keepalive:false,
                    open: true,
                    livereload: true
                }
            },
            //Testing targets
            root: {
                options: {
                    port: httpServerPort,
                    base:  '',
                    keepalive:false
                }
            },
            dev: {
                options: {
                    port: httpServerPort,
                    base:  '<%= fore.srcDir %>',
                    keepalive:false
                }
            },
            dist: {
                options: {
                    port: httpServerPort,
                    base:  '<%= fore.dist %>',
                    keepalive:false
                }
            }
        },

        jasmine: {
            pivotal: {
                src: 'app/scripts/*.js',
                options: {
                    specs: 'test/jasmine/*Spec.js'
                }
            }
        },

        karma: {
            options: {
                configFile: 'test/karma/conf/karma.conf.js',
                keepalive: true
            },
            buildbot: {
                reporters: ['crbot'],
                logLevel: 'OFF'
            },
            polymer: {
            }

        }

    });


    grunt.loadNpmTasks('grunt-karma');

    grunt.registerTask('createDevTarget', function( ) {
        if (!grunt.file.exists(foreConfig.webModule + foreConfig.devTarget)) {
            grunt.file.mkdir(foreConfig.webModule + foreConfig.devTarget+"/scripts");
            grunt.file.mkdir(foreConfig.webModule + foreConfig.pagesTarget);
        }
    });

    grunt.registerTask('default', [
        'rsync:developmentScripts',
        'rsync:developmentElements',
        'rsync:developmentPages',
        'less:development'
    ]);


    grunt.registerTask('dist', [
        'clean:dist',
        'useminPrepare',
        'imagemin',
        'concat',
        'uglify',
        'copy',
        'usemin',
        'less:dist',
        'htmlmin'


        // 'clean:webDevTarget'
        // ,'rsync:dist'

    ]);



    grunt.registerTask('test-dev', [
        'connect:dev',
        'dalek'
    ]);
    grunt.registerTask('test-dist', [
        'dist',
        'connect:dist',
        'dalek'
    ]);
    grunt.registerTask('test-js', [
        'connect:root',
        'jasmine'
    ]);
    grunt.registerTask('server',  [
        'connect:livereload',
        'watch'
    ]);
    grunt.registerTask('override-chrome-launcher', 'Enable Harmony for Chrome Canary', function() {
        var os = require('os').type();
        if (os === 'Darwin') {
            var exec = require('child_process').exec;
            var cb = this.async();
            exec('npm install --tmp ../.tmp git://github.com/morethanreal/karma-chrome-launcher',
                null, function(err, stdout, stderr) {
                    console.log(stdout);
                    console.log(stderr);
                    cb();
                });
        }
    });

    grunt.registerTask('test-karma', [
        'override-chrome-launcher',
        'karma:polymer'
    ]);
};

