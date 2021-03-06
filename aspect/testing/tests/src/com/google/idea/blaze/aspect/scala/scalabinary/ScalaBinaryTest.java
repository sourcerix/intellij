/*
 * Copyright 2017 The Bazel Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.idea.blaze.aspect.scala.scalabinary;

import static com.google.common.truth.Truth.assertThat;

import com.google.devtools.intellij.IntellijAspectTestFixtureOuterClass.IntellijAspectTestFixture;
import com.google.devtools.intellij.ideinfo.IntellijIdeInfo.TargetIdeInfo;
import com.google.idea.blaze.BazelIntellijAspectTest;
import com.google.idea.blaze.aspect.IntellijAspectTest;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests scala_binary */
@RunWith(JUnit4.class)
public class ScalaBinaryTest extends BazelIntellijAspectTest {
  @Test
  public void testScalaBinary() throws Exception {
    IntellijAspectTestFixture testFixture = loadTestFixture(":foo_fixture");
    TargetIdeInfo binaryInfo = findTarget(testFixture, ":foo");

    assertThat(binaryInfo.getKindString()).isEqualTo("scala_binary");
    assertThat(relativePathsForArtifacts(binaryInfo.getJavaIdeInfo().getSourcesList()))
        .containsExactly(testRelative("FooMain.scala"));
    assertThat(dependenciesForTarget(binaryInfo)).contains(dep(":foolib"));

    assertThat(
            binaryInfo
                .getJavaIdeInfo()
                .getJarsList()
                .stream()
                .map(IntellijAspectTest::libraryArtifactToString)
                .collect(Collectors.toList()))
        .containsExactly(jarString(testRelative("foo.jar"), null, null));

    assertThat(binaryInfo.getJavaIdeInfo().getMainClass()).isEqualTo("com.google.MyMainClass");

    assertThat(getOutputGroupFiles(testFixture, "intellij-info-java"))
        .containsAllOf(
            testRelative("foolib.intellij-info.txt"), testRelative("foo.intellij-info.txt"));
    assertThat(getOutputGroupFiles(testFixture, "intellij-resolve-java"))
        .containsAllOf(testRelative("foolib.jar"), testRelative("foo.jar"));
    assertThat(getOutputGroupFiles(testFixture, "intellij-compile-java"))
        .containsAllOf(testRelative("foolib.jar"), testRelative("foo.jar"));
    assertThat(getOutputGroupFiles(testFixture, "intellij-info-generic")).isEmpty();
  }
}
