/*
* Copyright 2013 the original author or authors
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
ruleset {
  description 'Rules For Gradle Release Plugin Project'
  ruleset('rulesets/basic.xml')
  ruleset('rulesets/braces.xml')
  ruleset('rulesets/concurrency.xml')
  ruleset('rulesets/convention.xml')
  ruleset('rulesets/design.xml') {
    AbstractClassWithoutAbstractMethod(enabled: false)
  }
  ruleset('rulesets/dry.xml') {
    DuplicateNumberLiteral(enabled: false)
    DuplicateStringLiteral(enabled: false) // too much hassle
    DuplicateListLiteral(enabled: false)
  }
  ruleset('rulesets/exceptions.xml')
  ruleset('rulesets/formatting.xml') {
    ClassJavadoc(enabled: false)
    SpaceAroundMapEntryColon(characterAfterColonRegex: /\s/)
    BlankLineBeforePackage(enabled: false)
  }
  // generic rules need to be configured to be active and useful
  //ruleset('rulesets/generic.xml')
  IllegalRegex {
    name = 'TODO'
    priority = 2
    regex = /TODO/
    description = 'TODOs should not be commited'
  }

  RequiredRegex {
    name = 'HeaderRequired'
    regex = /(?m)^\/\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*$\n^ \* Copyright 2016 Adobe Systems Incorporated\.$\n^ \*$\n^ \* <p>$\n^ \* Licensed under the Apache License, Version 2\.0 \(the "License"\);$\n^ \* you may not use this file except in compliance with the License\.$\n^ \* You may obtain a copy of the License at$\n^ \*$\n^ \* <p>$\n^ \*     http:\/\/www\.apache\.org\/licenses\/LICENSE-2\.0$\n^ \*$\n^ \* <p>$\n^ \* Unless required by applicable law or agreed to in writing, software$\n^ \* distributed under the License is distributed on an "AS IS" BASIS,$\n^ \* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied\.$\n^ \* See the License for the specific language governing permissions and$\n^ \* limitations under the License\.$\n^ \*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\/$/
    violationMessage = 'The header comment is missing from the source file'
    description = 'Checks that all source files contain the header'
  }

  RequiredRegex {
    name = 'AuthorRequired'
    regex = /@author Adobe Systems Inc\./
    violationMessage = 'The @author comment is missing from the source file'
    description = 'Checks that all source files contain the @author comment'
  }

  ruleset('rulesets/groovyism.xml') {
    GetterMethodCouldBeProperty(enabled: false)
  }
  ruleset('rulesets/imports.xml') {
    NoWildcardImports(enabled: false)
    MisorderedStaticImports(comesBefore: false)
  }
  ruleset('rulesets/junit.xml') {
    JUnitPublicNonTestMethod(enabled: false)
  }
  ruleset('rulesets/logging.xml')
  ruleset('rulesets/naming.xml') {
    FactoryMethodName(enabled: false)
    MethodName(enabled: false) // Spock method names
  }
  ruleset('rulesets/security.xml') {
    JavaIoPackageAccess(enabled: false) // gradle uses Java.io.File
  }
  ruleset('rulesets/serialization.xml')
  ruleset('rulesets/size.xml') {
    CrapMetric(enabled: false)
    AbcMetric(enabled: false)
  }
  ruleset('rulesets/unnecessary.xml') {
    UnnecessaryDefInMethodDeclaration(enabled: false)
    UnnecessaryGetter(enabled: false)
    UnnecessaryObjectReferences(enabled: false)
  }
  ruleset('rulesets/unused.xml')
}
