package com.xaidat.mavenplugins.tcsvcmsg;

import com.xaidat.mavenplugins.tcsvcmsg.TeamCityServiceMessagesMojo.ConfigurationMethod;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TeamCityServiceMessagesMojoTest {
  private static final SortedMap<String, String> PARAMS;

  static {
    SortedMap<String, String> params = new TreeMap<>();
    params.put("bar", "baz");
    params.put("ber", "bez");
    PARAMS = Collections.unmodifiableSortedMap(params);
  }

  @Test
  public void testMakeMessageValue() throws Exception {
    assertEquals("value-based message", "\n##teamcity[foo 'bar']\n",
                 TeamCityServiceMessagesMojo.makeMessage("foo", "bar", null, ConfigurationMethod.VALUE));
  }

  @Test
  public void testMakeMessageParams() throws Exception {
    assertEquals("parameter-based message", "\n##teamcity[foo bar='baz' ber='bez']\n",
                 TeamCityServiceMessagesMojo.makeMessage("foo", null, PARAMS, ConfigurationMethod.PARAMETERS));
  }

  @Ignore
  @Test
  public void testEscapeUnicode() throws Exception {
    // TODO interpretation of escape table at following URL is bad, but unclear which code points to escape, which
    // to show as chars?
    // https://confluence.jetbrains.com/display/TCD8/Build+Script+Interaction+with+TeamCity
    // #BuildScriptInteractionwithTeamCity-AddingorChangingaBuildParameter
    //    assertEquals("unexpected unicode escape", "|u9292|U0cF9", TeamCityServiceMessagesMojo.escape
    //            ("\u9292\u0cf9"));
  }

  @Test
  public void testEscapeOrdinary() throws Exception {
    assertEquals("unexpected ordinary escape", "Very |||| escape|' heavy|r|n|[text|]",
                 TeamCityServiceMessagesMojo.escape("Very || escape' heavy\r\n[text]"));
  }

  @Test(expected = MojoExecutionException.class)
  public void invalidBoth() throws Exception {
    TeamCityServiceMessagesMojo.validOrThrow("bla", "bir", PARAMS);
  }


  @Test(expected = MojoExecutionException.class)
  public void invalidNone() throws Exception {
    TeamCityServiceMessagesMojo.validOrThrow("bla", null, null);
  }


  @Test(expected = MojoExecutionException.class)
  public void invalidNoName() throws Exception {
    TeamCityServiceMessagesMojo.validOrThrow(null, "bir", null);
  }


  @Test
  public void validParams() throws Exception {
    assertSame("did not detect params config", ConfigurationMethod.PARAMETERS,
               TeamCityServiceMessagesMojo.validOrThrow("bla", null, PARAMS));
  }

  @Test
  public void validValue() throws Exception {
    assertSame("did not detect value config", ConfigurationMethod.VALUE,
               TeamCityServiceMessagesMojo.validOrThrow("bla", "bir", null));
  }
}
