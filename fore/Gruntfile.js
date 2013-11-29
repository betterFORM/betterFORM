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
        elements:'/elements'
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
                files:['<%= fore.srcDir %>/elements/**/*.js'],
                tasks: ['rsync:developmentElements']
            },
            elementsHTML: {
                files:['<%= fore.srcDir %>/elements/**/*.html'],
                tasks: ['rsync:developmentElements']
            },
            styles: {
                files:['<%= fore.srcDir %>/styles/*.less'],
                tasks: ['less:development']
            },
            images: {
                files:['<%= fore.srcDir %>/images/**/*'],
                tasks: ['rsync:developmentImages']
            },
            pages: {
                files:['<%= fore.srcDir %>/pages/**/*'],
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
                dest: '<%= webDevTarget %>/styles'
            },
            dist: {
                // no need for files, the config below should work
                expand: true,
                cwd: '<%= fore.srcDir %>/styles',
                src:    '*.less',
                ext:    '.css',                
                dest: '<%= fore.dist %>/styles'
            }
        },
        
        //USEMIN tasks
        useminPrepare: {
            html: '<%= fore.srcDir %>/build.html',
            options: {
                dest: '<%= fore.dist %>'
            }
        },
        
        usemin: {
            html: '<%= fore.dist %>/build.html',
            options: {
                dirs: ['<%= fore.dist %>']
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
        dalek: {
            options: {
                // invoke phantomjs, chrome & chrome canary
                browser: ['chrome', 'firefox','sauce'],
                reporter: ['html', 'junit'],
                driver: {
                    sauce: {
                        "user": "windauer",
                        "key": "b4e5c3ba-8b80-44e4-94dd-e62b04385c76"
                    }
                }
            },


            dist: {
                src: ['test/samples/test_google.js']
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
        'concat',
        'uglify',
        'usemin',
        'less:dist',
        'clean:webDevTarget',
        'rsync:dist'
    ]);
};

