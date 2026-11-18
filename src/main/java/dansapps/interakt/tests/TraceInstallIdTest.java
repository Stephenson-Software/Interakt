package dansapps.interakt.tests;

import dansapps.interakt.trace.TraceInstallId;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Characterization tests for where the trace installation ID comes from: the pinning environment
 * variable is trimmed and ignored when blank or too long, and the ID file sits in the platform's
 * user data directory under the lower-cased program name. The environment, operating system and
 * home directory are read through package-private seams, reached here by reflection so the vendored
 * file stays as published; nothing here reads the real environment or touches the disk.
 */
public class TraceInstallIdTest {
    private static final String HOME = new File("interakt-test-home").getAbsolutePath();

    private final Map<String, String> environment = new HashMap<>();

    @Test
    public void testUnsetVariableGivesNoId() throws Exception {
        Assert.assertNull(fromEnvironment());
    }

    @Test
    public void testBlankVariableGivesNoId() throws Exception {
        environment.put(TraceInstallId.ENV, "   ");

        Assert.assertNull(fromEnvironment());
    }

    @Test
    public void testVariableIsTrimmed() throws Exception {
        environment.put(TraceInstallId.ENV, "  pinned-id  ");

        Assert.assertEquals("pinned-id", fromEnvironment());
    }

    @Test
    public void testIdOfTheMaximumLengthIsKept() throws Exception {
        String id = repeat('a', 255);
        environment.put(TraceInstallId.ENV, " " + id + " ");

        Assert.assertEquals(id, fromEnvironment());
    }

    @Test
    public void testIdLongerThanTheMaximumIsDropped() throws Exception {
        environment.put(TraceInstallId.ENV, repeat('a', 256));

        Assert.assertNull(fromEnvironment());
    }

    @Test
    public void testWindowsUsesAppData() throws Exception {
        String appData = new File("interakt-test-appdata").getAbsolutePath();
        environment.put("APPDATA", appData);

        Assert.assertEquals(new File(appData), userDataDirectory("Windows 11"));
    }

    @Test
    public void testWindowsWithoutAppDataFallsBackToRoamingUnderHome() throws Exception {
        environment.put("APPDATA", "  ");
        File expected = new File(new File(new File(HOME), "AppData"), "Roaming");

        Assert.assertEquals(expected, userDataDirectory("Windows 10"));
    }

    @Test
    public void testWindowsIgnoresXdgDataHome() throws Exception {
        environment.put("XDG_DATA_HOME", new File("interakt-test-xdg").getAbsolutePath());
        File expected = new File(new File(new File(HOME), "AppData"), "Roaming");

        Assert.assertEquals(expected, userDataDirectory("Windows 10"));
    }

    @Test
    public void testMacUsesApplicationSupport() throws Exception {
        environment.put("XDG_DATA_HOME", new File("interakt-test-xdg").getAbsolutePath());
        File expected = new File(new File(new File(HOME), "Library"), "Application Support");

        Assert.assertEquals(expected, userDataDirectory("Mac OS X"));
        Assert.assertEquals(expected, userDataDirectory("Darwin"));
    }

    @Test
    public void testLinuxUsesAnAbsoluteXdgDataHome() throws Exception {
        String xdg = new File("interakt-test-xdg").getAbsolutePath();
        environment.put("XDG_DATA_HOME", xdg);

        Assert.assertEquals(new File(xdg), userDataDirectory("Linux"));
    }

    @Test
    public void testLinuxIgnoresARelativeXdgDataHome() throws Exception {
        environment.put("XDG_DATA_HOME", "relative/xdg");

        Assert.assertEquals(localShare(), userDataDirectory("Linux"));
    }

    @Test
    public void testLinuxWithoutXdgDataHomeUsesLocalShare() throws Exception {
        Assert.assertEquals(localShare(), userDataDirectory("Linux"));
    }

    @Test
    public void testUnknownOrMissingOperatingSystemIsTreatedLikeLinux() throws Exception {
        Assert.assertEquals(localShare(), userDataDirectory("FreeBSD"));
        Assert.assertEquals(localShare(), userDataDirectory(null));
    }

    @Test
    public void testFileIsUnderTheTrimmedLowerCasedProgramName() throws Exception {
        File expected = new File(new File(localShare(), "interakt"), TraceInstallId.FILE_NAME);

        Assert.assertEquals(expected, file("  Interakt "));
    }

    @Test
    public void testPublicFileKeepsTheSameShape() {
        File idFile = TraceInstallId.file("Interakt");

        Assert.assertEquals(TraceInstallId.FILE_NAME, idFile.getName());
        Assert.assertEquals("interakt", idFile.getParentFile().getName());
    }

    private File localShare() {
        return new File(new File(new File(HOME), ".local"), "share");
    }

    private String fromEnvironment() throws Exception {
        Method method = TraceInstallId.class.getDeclaredMethod("fromEnvironment", Function.class);
        return (String) invoke(method, (Function<String, String>) environment::get);
    }

    private File userDataDirectory(String osName) throws Exception {
        Method method = TraceInstallId.class.getDeclaredMethod("userDataDirectory",
                Function.class, String.class, String.class);
        return (File) invoke(method, (Function<String, String>) environment::get, osName, HOME);
    }

    private File file(String programName) throws Exception {
        Method method = TraceInstallId.class.getDeclaredMethod("file",
                String.class, Function.class, String.class, String.class);
        return (File) invoke(method, programName, (Function<String, String>) environment::get, "Linux", HOME);
    }

    private Object invoke(Method method, Object... arguments) throws Exception {
        method.setAccessible(true);
        try {
            return method.invoke(null, arguments);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    private String repeat(char character, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(character);
        }
        return builder.toString();
    }
}
