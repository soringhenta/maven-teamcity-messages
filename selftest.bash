#!/usr/bin/env bash

mvn install >& /dev/null || exit 1

mvn -P selftest-works-parameters integration-test 2> /dev/null | fgrep "##teamcity[bla bar='/Users/beha/Projects/devops/maven-teamcity-messages/target/classes']" > /dev/null && echo OK works-param || echo FAIL works-param
mvn -P selftest-works-value integration-test 2> /dev/null | fgrep "##teamcity[bla 'model version is 4.0.0']" > /dev/null && echo OK works-value || echo FAIL works-value

for failtest in selftest-fails-noname selftest-fails-none selftest-fails-both selftest-fails-multiproblem; do
  mvn -P $failtest integration-test >& /dev/null && echo FAIL $failtest || echo OK $failtest
done

