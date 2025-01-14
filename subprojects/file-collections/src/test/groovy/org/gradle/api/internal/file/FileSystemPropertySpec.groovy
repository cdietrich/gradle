/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.file

import org.gradle.api.file.FileSystemLocation
import org.gradle.api.internal.provider.PropertySpec
import org.gradle.api.internal.provider.Providers
import org.gradle.test.fixtures.file.TestNameTestDirectoryProvider
import org.junit.Rule

abstract class FileSystemPropertySpec<T extends FileSystemLocation> extends PropertySpec<T> {
    @Rule
    TestNameTestDirectoryProvider tmpDir = new TestNameTestDirectoryProvider()
    def resolver = TestFiles.resolver(tmpDir.testDirectory)
    def factory = new DefaultFilePropertyFactory(resolver)
    def baseDir = factory.newDirectoryProperty()

    def setup() {
        baseDir.set(tmpDir.testDirectory)
    }

    def "can set value using absolute file"() {
        given:
        def file = tmpDir.file("thing")
        def prop = providerWithNoValue()
        prop.set(file)

        expect:
        prop.get().asFile == file
    }

    def "can set value using relative file"() {
        given:
        def file = new File("thing")
        def prop = providerWithNoValue()
        prop.set(file)

        expect:
        prop.get().asFile == tmpDir.file("thing")
    }

    def "can set value using absolute file provider"() {
        given:
        def file = tmpDir.file("thing")
        def prop = providerWithNoValue()
        prop.fileProvider(Providers.of(file))

        expect:
        prop.get().asFile == file
    }

    def "can set value using relative file provider"() {
        given:
        def file = new File("thing")
        def prop = providerWithNoValue()
        prop.fileProvider(Providers.of(file))

        expect:
        prop.get().asFile == tmpDir.file("thing")
    }

    def "cannot set value using file when finalized"() {
        given:
        def file = tmpDir.file("thing")
        def prop = providerWithNoValue()
        prop.finalizeValue()

        when:
        prop.set(file)

        then:
        def e = thrown(IllegalStateException)
        e.message == 'The value for this property is final and cannot be changed any further.'
    }

    def "cannot set value using file when changes disallowed"() {
        given:
        def file = tmpDir.file("thing")
        def prop = providerWithNoValue()
        prop.disallowChanges()

        when:
        prop.set(file)

        then:
        def e = thrown(IllegalStateException)
        e.message == 'The value for this property cannot be changed any further.'
    }
}
