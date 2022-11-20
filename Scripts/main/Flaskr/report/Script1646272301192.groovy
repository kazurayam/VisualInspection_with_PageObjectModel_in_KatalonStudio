import java.nio.file.Files
import java.nio.file.Path

import com.kazurayam.materialstore.base.inspector.Inspector
import com.kazurayam.materialstore.core.filesystem.JobName
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

/**
 * Test Cases/main/Flaskr/report
 */
assert store != null
assert mProductGroup != null
assert mProductGroup.isReadyToReport()
assert sortKeys != null
assert criteria != null

JobName jobName = mProductGroup.getJobName()

WebUI.comment("report started; store=${store}")
println "mProductGroup=" + mProductGroup.toSummary()

// the file name of HTML report
String fileName = jobName.toString()+ "-index.html"

Inspector inspector = Inspector.newInstance(store)
inspector.setSortKeys(sortKeys)
Path report = inspector.report(mProductGroup, criteria, fileName)

assert Files.exists(report)
WebUI.comment("The report can be found at ${report.toString()}")

int warnings = mProductGroup.countWarnings(criteria)
return warnings
