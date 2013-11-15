'use strict';
module.exports = function(grunt) {
    // load all grunt tasks
    require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);
    require('time-grunt')(grunt);

    var foreConfig = {
        srcDir: 'app',
        webDir: '../web',
        dist: 'dist',
        devTarget: '/target/betterform/WEB-INF/classes/META-INF/resources'
    };

    grunt.initConfig({
        fore: foreConfig,
        webDevTarget: foreConfig.webDir + foreConfig.devTarget,
        
        //WATCH tasks
        watch: {
            options: {
                nospawn: true
            },
            elementsScripts : {
                files:['<%= fore.srcDir %>/elements/**/*.js'],
                tasks: ['rsync:developmentElements']
            },
            elementsHTML : {
                files:['<%= fore.srcDir %>/elements/**/*.html'],
                tasks: ['rsync:developmentElements']
            },
            styles : {
                files:['<%= fore.srcDir %>/styles/*.less'],
                tasks: ['less:development']
            },
            images : {
                files:['<%= fore.srcDir %>/images/**/*'],
                tasks: ['rsync:developmentImages']
            }
        },
        
        //RSYNC tasks
        rsync: {
            options: {
                args: ["-vc"],
                recursive: true
            },
            developmentScripts: {
                options: {
                    // !!! The last "/" is IMPORTANT here!!!!
                    src: '<%= fore.srcDir %>/bower_components/',
                    dest: '<%= webDevTarget %>/scripts/'
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
            dist: {
                options: {
                    // !!! The last "/" is IMPORTANT here!!!!
                    src: ['<%= fore.dist %>/', '<%= fore.srcDir %>/elements'],
                    dest: '<%= webDevTarget %>',
                    exclude: 'build.html'
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
        }
    });


    grunt.registerTask('createDevTarget', function() {
        if (!grunt.file.exists(foreConfig.webdir + foreConfig.devTarget)) {
            grunt.file.mkdir(foreConfig.webDir + foreConfig.devTarget+"/scripts");
        }
    });

    grunt.registerTask('default', [
        'createDevTarget',
        'rsync:developmentScripts',
        'rsync:developmentElements',
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
