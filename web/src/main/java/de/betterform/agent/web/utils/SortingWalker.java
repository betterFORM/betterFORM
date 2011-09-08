/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.utils;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * @author <a href="mailto:tobias.krebs@betterform.de">tobi</a>
 * @version $Id: de.betterform.agent.web.utils.SortingWalker 08.11.2010 tobi $
 */
public class SortingWalker extends DirectoryWalker {
    private String[] fileExtensions = null;
    private boolean recursive = false;
    private int depthLimit = -1;
    private Comparator defaultComparator = NameFileComparator.NAME_INSENSITIVE_COMPARATOR;

    private SortingWalker(int depth) {
        super(null, depth);
        this.depthLimit = depth;
    }

     private SortingWalker(int depth, String[] fileExtensions) {
        super(null, depth);
        this.depthLimit = depth;
        this.fileExtensions = fileExtensions;
    }

    /*
     * Overriden DirectoryWalker methods
     */
    @Override
    protected boolean handleDirectory(File directory, int depth, Collection results) {
        //ignore root directory
        if (depth == 0) {
            return true;
        } else if (this.depthLimit >= depth) {
            results.add(directory);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void handleFile(File directory, int depth, Collection results) {
        //Do nothing on files.
    }


    /**
     * Sort all directories/files in given directory,
     * All files will be listed.
     * Search depth is root directory
     * @param directory directory to scan for Files
     */
    public static List<File> sortDirsAndFiles(File directory) throws IOException {
        return sortDirsAndFiles(directory, 1, null);
    }

    /**
     * Sort all directories/files in given directory,
     * Only files matching the filesExtensions will be listed.
     * Search depth is root directory
     * @param directory directory to scan for Files
     * @param fileExtensions File-extension which should be considered, "null" for all files
     */
    public static List<File> sortDirsAndFiles(File directory, String[] fileExtensions) throws IOException {
        return sortDirsAndFiles(directory, 1, fileExtensions);
    }

    /*
     * Sort all directories/files in given directory,
     * Only files matching the filesExtensions will be listed.
     * @param directory directory to scan for Files
     * @param fileExtensions File-extension which should be considered, "null" for all files
     * @param depth for directory search.
     */
    private static List<File> sortDirsAndFiles(File directory, int depth, String[] fileExtensions) throws IOException {
        SortingWalker walker = new SortingWalker(depth, fileExtensions);
        return walker.sortDir(directory);
    }


    /*
     * Start in root-directory and search top-level directories and files.
     * First sort directories by name (CASE_INSENSITIVE).
     * Then all files in root-Dir by name (CASE_INSENSITIVE).
     */
    private List<File> sortDir(File root) throws IOException {
        ArrayList<File> resultDirectoriesList = new ArrayList<File>();
        ArrayList<File> results = new ArrayList<File>();
        //TODO: deep search via depth
        walk(root, resultDirectoriesList);
        Collections.sort(resultDirectoriesList, defaultComparator);
        results.addAll(resultDirectoriesList);
        results.addAll(sortFilesInDir(root));

        return results;
    }



    private List sortFilesInDir(File directory) {
        List<File> list = new ArrayList(FileUtils.listFiles(directory, this.fileExtensions, this.recursive));
        Collections.sort(list, defaultComparator);
        return list;
    }
}
