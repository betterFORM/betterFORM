'use strict';
var LIVERELOAD_PORT = 35729;
var lrSnippet = require('connect-livereload')({port: LIVERELOAD_PORT});
var mountFolder = function (connect, dir) {
    return connect.static(require('path').resolve(dir));
};

// # Globbing
// for performance reasons we're only matching one level down:
// 'test/spec/{,*/}*.js'
// use this if you want to match all subfolders:
// 'test/spec/**/*.js'

module.exports = function (grunt) {
    // load all grunt tasks
    require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);
    require('time-grunt')(grunt);

    // configurable paths
    var yeomanConfig = {
        app: 'app',
        dist: 'dist'
    };

    var foreConfig = {
        newFile: '',
        target: '../../web/target/betterform/'
    }

    grunt.initConfig({
        yeoman: yeomanConfig,
        fore: foreConfig,

        watch: {
            options: {
            nospawn: true
            //    livereload: true
            },

            dummy : {
                files:['<%= yeoman.app%>/robots.txt']
            },
            forms : {
                files:['<%= yeoman.app%>/forms/{,*/}*.xhtml'],
                tasks: ['rsync:forms']
            },
            script : {
                files:['<%= yeoman.app%>/scripts/**/*.js'],
                tasks: ['rsync:scripts']
            },
            style : {
                files:['<%= yeoman.app%>/styles/*.less'],
                tasks: ['less:development']
            },
            images : {
                files:['<%= yeoman.app%>/images/**/*'],
                tasks: ['rsync:images']
            }
        },
        // command: 'node bin/vulcanize -csp -i app/layouter.html -o dist/layouter.html',
        exec: {
            vulcan: {
                command: 'node node_modules/vulcanize/bin/vulcanize -csp -i dist/index.xhtml -o dist/build.xhtml',
                stdout: true,
                stderr: true
            }
        },
        connect: {
            options: {
                port: 9000,
                // change this to '0.0.0.0' to access the server from outside
                hostname: 'localhost'
            },
            livereload: {
                options: {
                    middleware: function (connect) {
                        return [
                            lrSnippet,
                            mountFolder(connect, '.tmp'),
                            mountFolder(connect, yeomanConfig.app)
                        ];
                    }
                }
            },
            test: {
                options: {
                    middleware: function (connect) {
                        return [
                            mountFolder(connect, '.tmp'),
                            mountFolder(connect, 'test'),
                            mountFolder(connect, yeomanConfig.app)
                        ];
                    }
                }
            },
            dist: {
                options: {
                    middleware: function (connect) {
                        return [
                            mountFolder(connect, yeomanConfig.dist)
                        ];
                    }
                }
            }
        },
        open: {
            server: {
                path: 'http://localhost:<%= connect.options.port %>'
            }
        },
        clean: {
            dist: ['.tmp', '<%= yeoman.dist %>/*'],
            server: '.tmp'
        },
        jshint: {
            options: {
                jshintrc: '.jshintrc'
            },
            all: [
                'Gruntfile.js',
                '<%= yeoman.app %>/scripts/{,*/}*.js',
                '!<%= yeoman.app %>/scripts/vendor/*',
                'test/spec/{,*/}*.js'
            ]
        },
        mocha: {
            all: {
                options: {
                    run: true,
                    urls: ['http://localhost:<%= connect.options.port %>/index.html']
                }
            }
        },
    
        useminPrepare: {
            html: '<%= yeoman.app %>/index.xhtml',
            options: {
                dest: '<%= yeoman.dist %>'
            }
        },
        usemin: {
            html: ['<%= yeoman.dist %>/{,*/}*.xhtml'],
            css: ['<%= yeoman.dist %>/styles/{,*/}*.css'],
            options: {
                dirs: ['<%= yeoman.dist %>']
            }
        },
        imagemin: {
            dist: {
                files: [{
                    expand: true,
                    cwd: '<%= yeoman.app %>/images',
                    src: '{,*/}*.{png,jpg,jpeg}',
                    dest: '<%= yeoman.dist %>/images'
                }]
            }
        },
        cssmin: {
            dist: {
                files: {
                    '<%= yeoman.dist %>/styles/main.css': [
                        '.tmp/styles/{,*/}*.css',
                        '<%= yeoman.app %>/styles/{,*/}*.css'
                    ]
                }
            }
        },
        htmlcompressor : {

            dist: {
                options: {
                    preserveLineBreaks: true
                },
                files: [{
                    expand: true,
                    cwd: '<%= yeoman.dist %>',
                    src: '*.html',
                    dest: '<%= yeoman.dist %>'
                }]
            }
        },
        htmlmin: {
            dist: {
                options: {
                    removeOptionalTags: false,
                    removeEmptyAttributes: false
                    /*removeCommentsFromCDATA: true,
                    // https://github.com/yeoman/grunt-usemin/issues/44
                    //collapseWhitespace: true,
                    collapseBooleanAttributes: true,
                    removeAttributeQuotes: true,
                    removeRedundantAttributes: true,
                    useShortDoctype: true,
                    removeEmptyAttributes: true,
                    removeOptionalTags: true*/
                },
                files: [{
                    expand: true,
                    cwd: '<%= yeoman.app %>',
                    src: '*.html',
                    dest: '<%= yeoman.dist %>'
                }]
            }
        },
        uglify: {
            'dist/elements/bf-app.js' : '<%= yeoman.app %>/elements/bf-app.js',
            'dist/elements/bf-spread.js' : '<%= yeoman.app %>/elements/bf-spread.js',
            'dist/elements/styles.js' : '<%= yeoman.app %>/elements/styles.js'
        },
        copy: {
            dist: {
                files: [{
                    expand: true,
                    dot: true,
                    cwd: '<%= yeoman.app %>',
                    dest: '<%= yeoman.dist %>',
                    src: [
                        '*.{ico,txt}',
                        '.htaccess',
                        'elements/**',
                        'lib-elements/**',
                        'images/{,*/}*.{webp,gif}',
                        '*.xhtml',
                        '*.html'
                    ]
                }]
            },
            new: {
                files: [{
                    expand: true,
                    src: '<%= fore.newFile %>',
                    dest: '../../web/target/betterform/forms/ng'
                }]
            }
        },
        bower: {
            all: {
                rjsConfig: '<%= yeoman.app %>/scripts/main.js'
            }
        },

        rsync: {
            options: {
                args: ["--verbose"]
            },
            forms: {
                options: {
                    recursive: true,
                    src: '<%= yeoman.app%>/forms/',
                    dest: '<%= fore.target %>forms'
                }
            },
            images: {
                options: {
                    src: '<%= yeoman.app%>/images/',
                    dest: '<%= fore.target %>resources/images'
                }
            },
            scripts: {
                options: {
                    src: '<%= yeoman.app%>/scripts/',
                    dest: '<%= fore.target %>resources/scripts'
                }
            }
        },


        less: {
            development: {
            options: {
                paths: ['<%= yeoman.app%>/styles']
            },
            // target name
            development: {
                // no need for files, the config below should work
                expand: true,
                cwd:    '<%= yeoman.app%>/styles',
                src:    '*.less',
                ext:    '.css',
                dest: '<%= fore.target %>resources/styles'
            }
            }
        }
    });

    grunt.registerTask('server', function (target) {
        if (target === 'dist') {
            return grunt.task.run(['build', 'open', 'connect:dist:keepalive']);
        }

        grunt.task.run([
            'clean:server',
            
            'connect:livereload',
            'copy',
            'open',
            'watch'
        ]);
    });

    grunt.registerTask('wcbuild',[
        'exec:vulcan'
    ]);

    grunt.registerTask('test', [
        'clean:server',
        
        
        'connect:test',
        'mocha'
    ]);

    grunt.registerTask('build', [
        'clean:dist',
        'useminPrepare',
        'imagemin',
        'concat',
        'cssmin',
        'copy:dist',
        'usemin',
        'wcbuild',
        'htmlmin'
    ]);

    grunt.registerTask('default', [
        'jshint',
        'test',
        'build'
    ]);
};
