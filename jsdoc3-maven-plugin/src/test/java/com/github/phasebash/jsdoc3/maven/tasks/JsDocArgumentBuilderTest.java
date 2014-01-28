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
import java.util.List;

public class JsDocArgumentBuilderTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private TaskContext minimumContext;

    private TaskContext.Builder minimumBuilder;

    private JsDocArgumentBuilder builder;

    private static final String JS_DOC_DIR_NAME = "jzdocz";

    @Before
    public void setUp() {
        builder = new JsDocArgumentBuilder();

        minimumBuilder = new TaskContext.Builder()
                .withDirectoryRoots(new File[] { temporaryFolder.newFolder("dir1") })
                .withJsDocDirectory(temporaryFolder.newFolder(JS_DOC_DIR_NAME))
                .withOutputDirectory(temporaryFolder.newFolder("output"))
                .withTempDirectory(temporaryFolder.newFolder("temp"));

        minimumContext = minimumBuilder.build();
    }

    @Test
    public void testMinimalContext() {
        List<String> arguments = builder.build(minimumContext);
        Assert.assertEquals("number of arguments.", 17, arguments.size());

        assertClassPath(arguments);
        assertModule(arguments);
        assertJsDocJs(arguments);
        assertDirName(arguments);

        Assert.assertFalse(arguments.contains("-l"));
    }

    @Test
    public void testLenientContext() {
        TaskContext.Builder lenientBuilder = new TaskContext.Builder(minimumBuilder);
        Assert.assertTrue(builder.build(lenientBuilder.withLeniency(true).build()).contains("-l"));
    }

    @Test
    public void testContextWithConfig() {
        TaskContext.Builder configBuilder = new TaskContext.Builder(minimumBuilder).withConfigFile(new File("config"));
        Assert.assertTrue(builder.build(configBuilder.build()).contains("-c"));
    }

    @Test
    public void testContextWithIncludePrivate() {
        TaskContext.Builder configBuilder = new TaskContext.Builder(minimumBuilder).withIncludePrivate(true);
        Assert.assertTrue(builder.build(configBuilder.build()).contains("-p"));
    }

    @Test
    public void testContextWithTemplate() {
        TaskContext.Builder configBuilder = new TaskContext.Builder(minimumBuilder)
            .withTemplateDirectory(new File("foo/bar"));

        Assert.assertTrue(builder.build(configBuilder.build()).contains("-t"));
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
