'use strict';
module.exports = function(grunt) {
    // load all grunt tasks
    require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);
    require('time-grunt')(grunt);

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
                nospawn: true
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
                src: ['test/dalekjs/test_index_page.js']
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
            livedev: {
                options: {
                    port: 9001,
                    base:  '<%= fore.srcDir %>',
                    keepalive:true
                }
            },
            dev: {
                options: {
                    port: 9001,
                    base:  '<%= fore.srcDir %>',
                    keepalive:false
                }
            },

            dist: {
                options: {
                    port: 9001,
                    base:  '<%= fore.dist %>',
                    keepalive:false
                }
            }
        }
    });


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
        'dist',
        'connect:dev',
        'dalek'
    ]);
    grunt.registerTask('test-dist', [
        'dist',
        'connect:dist',
        'dalek'
    ]);
};

