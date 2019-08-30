/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package team.itome.accessibilityanalyzer

import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckPreset
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult.AccessibilityCheckResultType
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheck
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult
import com.google.android.apps.common.testing.accessibility.framework.uielement.AccessibilityHierarchy
import com.google.android.apps.common.testing.accessibility.framework.uielement.proto.AccessibilityHierarchyProtos.AccessibilityHierarchyProto
import java.io.File
import java.io.FileNotFoundException
import java.util.*

private const val OUTPUT_DIR =
  "/Users/s04407/OSS/android-sunflower/app/build/outputs/apk/debug/crawl_output/app_firebase_test_lab"

fun main(args: Array<String>) {

  val files = File(OUTPUT_DIR).listFiles()
    ?.filter { Regex("accessibility[0-9]+.meta").matches(it.name) }
    ?: throw FileNotFoundException("No test target file found in $OUTPUT_DIR")

  for (file in files) {
    val proto = file.inputStream().use { stream -> AccessibilityHierarchyProto.parseFrom(stream) }
    val hierarchy = AccessibilityHierarchy.newBuilder(proto).build()
    val results = runAccessibilityChecks(hierarchy)

    results
      .filter {
        it.type == AccessibilityCheckResultType.ERROR ||
            it.type == AccessibilityCheckResultType.WARNING
      }
      .forEach {
        @Suppress("UNCHECKED_CAST")
        val checkClass = AccessibilityCheckPreset.getHierarchyCheckForClass(it.sourceCheckClass as Class<out AccessibilityHierarchyCheck>)
        println(checkClass.getMessageForResult(Locale.JAPANESE, it))
      }
//      .map { it.toProto() }
//      .forEachIndexed { index, outputProto ->
//        val file = File(dir, "${inputFile.nameWithoutExtension}_result$index.meta")
//        file.createNewFile()
//        file.outputStream().use { outputProto.writeTo(it) }
//      }
  }
}

private fun runAccessibilityChecks(
  hierarchy: AccessibilityHierarchy
): List<AccessibilityHierarchyCheckResult> {
  return AccessibilityCheckPreset
    .getAccessibilityHierarchyChecksForPreset(AccessibilityCheckPreset.LATEST)
    .flatMap { it.runCheckOnHierarchy(hierarchy) }
}
