/*
 *  Copyright (c) 2013 OCLC, Inc. All Rights Reserved.
 *
 *  OCLC proprietary information: the enclosed materials contain proprietary information of OCLC Online Computer
 *  Library Center, Inc. and shall not be disclosed in whole or in any part to any third party or used by any person
 *  for any purpose, without written consent of OCLC, Inc.  Duplication of any portion of these materials shall include
 *  this notice.
 */
package com.github.phasebash.jsdoc3.maven.tasks;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class JsDocArgumentBuilderTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private TaskContext minimumContext;

    private JsDocArgumentBuilder builder;
    
    private static final String JS_DOC_DIR_NAME = "jzdocz";

    @Before
    public void setUp() {
        builder = new JsDocArgumentBuilder();

        minimumContext = new TaskContext.Builder()
                .withDirectoryRoots(new File[] { temporaryFolder.newFolder("dir1") })
                .withJsDocDirectory(temporaryFolder.newFolder(JS_DOC_DIR_NAME))
                .withOutputDirectory(temporaryFolder.newFolder("output"))
                .withTempDirectory(temporaryFolder.newFolder("temp")).build();
    }

    @Test
    public void testMinimalContext() {
        List<String> arguments = builder.build(minimumContext);
        Assert.assertEquals("number of arguments.", 17, arguments.size());

        assertClassPath(arguments);
        assertModule(arguments);
        assertJsDocJs(arguments);
        assertDirName(arguments);
    }

    private void assertDirName(List<String> arguments) {
        Assert.assertEquals("dirname", "--dirname=" + path("/", directory(), JS_DOC_DIR_NAME), arguments.get(13));
    }

    private void assertJsDocJs(List<String> arguments) {
        Assert.assertEquals("jsdoc.js", path("/", directory(), JS_DOC_DIR_NAME, "jsdoc.js"), arguments.get(12));
    }

    private String osUriBase() {
        return File.separator.equals("\\") ? "file:/" : "file://";
    }

    private void assertModule(List<String> arguments) {
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals("module flag", "-modules", arguments.get(4 + (i * 2)));
        }

        String base = directory();

        Assert.assertEquals("module dir", osUriBase() + path("/", base, JS_DOC_DIR_NAME, "node_modules"), arguments.get(5));
        Assert.assertEquals("module dir", osUriBase() + path("/", base, JS_DOC_DIR_NAME, "rhino"), arguments.get(7));
        Assert.assertEquals("module dir", osUriBase() + path("/", base, JS_DOC_DIR_NAME, "lib"), arguments.get(9));
        Assert.assertEquals("module dir", osUriBase() + path("/", base, JS_DOC_DIR_NAME), arguments.get(11));
    }

    private void assertClassPath(List<String> arguments) {
        Assert.assertEquals("command", "java", arguments.get(0));
        Assert.assertEquals("classpath", "-classpath", arguments.get(1));
        Assert.assertEquals("classpath",  path("/", directory(), JS_DOC_DIR_NAME, "rhino", "js.jar"), arguments.get(2));
    }

    private String path(String separator, String... pieces) {
        return StringUtils.join(pieces, separator);
    }

    private String directory() {
        return temporaryFolder.getRoot().toString().replace("\\", "/");
    }
}
