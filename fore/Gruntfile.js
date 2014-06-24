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

    grunt.initConfig({
        //WATCH tasks
        watch: {
            options: {
                nospawn: true,
                livereload: true
            },
            elementsScripts: {
                files: ['fore-elements/**'],
                tasks: ['rsync:developmentElements']
            },
            target: {
                options: {
                    livereload: true
                },
                files: ['**/*.html']
            }
        },

        connect: {
            //Run "app" in grunt server
            livereload: {
                options: {
                    port: httpServerPort,
                    base:  '.',
                    keepalive:false,
                    open: true,
                    livereload: true
                }
            }
        },

        rsync: {
            options: {
                args: ["-vpc"],
                recursive: true
            },
            developmentElements: {
                options: {
                    src: 'fore-elements/**',
                    dest: 'components/'
                }
            }
        }
    });

    grunt.registerTask('server',  [
        'connect:livereload',
        'watch'
    ]);
};

