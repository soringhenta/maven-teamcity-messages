package com.xaidat.mavenplugins.tcsvcmsg;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Mojo that prints TeamCity service messages on the log. A TeamCity service message is a string in one of the
 * formats <pre>##teamcity[<messageName> 'value']</pre> for single-parameter messages or
 * <pre>##teamcity[<messageName> name1='value1' name2='value2']</pre> for multi-parameter messages.
 */
@Mojo(name = "printTeamcityServiceMessage")
public class TeamCityServiceMessagesMojo extends AbstractMojo {
  @Parameter
  private String name;

  @Parameter
  private String value;

  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  @Parameter
  private Map<String, String> parameters;


  /**
   * {@inheritDoc}
   *
   * @throws MojoExecutionException If configuration issues are detected. The exception message provides details on
   *                                the detected configuration issue(s).
   */
  public void execute() throws MojoExecutionException {
    final SortedMap<String, String> params =
            parameters == null ? null : Collections.unmodifiableSortedMap(new TreeMap<>(parameters));
    final ConfigurationMethod configurationMethod = validOrThrow(name, value, params);
    final String message = makeMessage(name, value, params, configurationMethod);
    getLog().info(message);
  }


  static String makeMessage(final String name, final String value, final SortedMap<String, String> parameters,
                            final ConfigurationMethod configurationMethod) {
    StringBuilder messageBuilder = new StringBuilder("\n##teamcity[").append(name);
    switch (configurationMethod) {
      case PARAMETERS:
        for (Entry<String, String> parameter : parameters.entrySet()) {
          messageBuilder.append(String.format(" %s='%s'", parameter.getKey(), escape(parameter.getValue())));
        }
        break;
      case VALUE:
        messageBuilder.append(" \'").append(escape(value)).append('\'');
        break;
      default:
        assert false : "Should be unreachable: Unknown configuration method.";
    }
    return messageBuilder.append("]\n").toString();
  }


  static String escape(final String value) {
    return value.replaceAll("\\\\([uU][\\da-fA-F]{4})", "|\\1").replace("|", "||").replace("'", "|'")
            .replace("\n", "|n").replace("\r", "|r").replace("[", "|[").replace("]", "|]");
  }


  static ConfigurationMethod validOrThrow(final String name, final String value, final Map<String, String> parameters)
          throws MojoExecutionException {
    final boolean viaValue = value != null;
    final boolean viaMap = parameters != null;

    final ArrayList<String> messages = new ArrayList<>();

    if (name == null) {
      messages.add("Desired service message name not set.");
    }

    if (viaValue && viaMap) {
      messages.add("Both parameters and value provided.");
    } else if (!viaValue && !viaMap) {
      messages.add("Neither parameters nor value provided.");
    }

    if (messages.size() != 0) {
      StringBuilder message = new StringBuilder("Configuration issue");
      if (messages.size() > 1) {
        message.append('s');
      }
      message.append(" detected:");
      for (String msg : messages) {
        message.append("\n").append(msg);
      }
      throw new MojoExecutionException(message.toString());
    }

    return viaValue ? ConfigurationMethod.VALUE : ConfigurationMethod.PARAMETERS;
  }


  enum ConfigurationMethod {
    VALUE, PARAMETERS
  }
}
