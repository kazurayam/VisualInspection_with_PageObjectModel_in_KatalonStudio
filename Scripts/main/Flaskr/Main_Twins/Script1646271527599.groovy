import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import java.nio.file.Path
import java.nio.file.Paths

import com.kazurayam.ks.globalvariable.ExecutionProfilesLoader
import com.kazurayam.materialstore.base.reduce.MaterialProductGroup
import com.kazurayam.materialstore.core.filesystem.JobName
import com.kazurayam.materialstore.core.filesystem.JobTimestamp
import com.kazurayam.materialstore.core.filesystem.MaterialList
import com.kazurayam.materialstore.core.filesystem.QueryOnMetadata
import com.kazurayam.materialstore.core.filesystem.SortKeys
import com.kazurayam.materialstore.core.filesystem.Store
import com.kazurayam.materialstore.core.filesystem.Stores
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

/**
 * Test Cases/main/Flaskr/Main_Twins
 *
 */

Path projectDir = Paths.get(RunConfiguration.getProjectDir())
Path root = projectDir.resolve("store")
Store store = Stores.newInstance(root)
JobName jobName = new JobName("Flaskr_Main_Twins")

ExecutionProfilesLoader profilesLoader = new ExecutionProfilesLoader()


//---------------------------------------------------------------------
/*
 * Materialize stage
 */
// visit the Production environment
String profile1 = "Flaskr_ProductionEnv"
profilesLoader.loadProfile(profile1)
WebUI.comment("Execution Profile ${profile1} was loaded with GlobalVariable.URL=${GlobalVariable.URL}")
JobTimestamp timestampP = JobTimestamp.now()

WebUI.callTestCase(
	findTestCase("main/Flaskr/materialize"),
	["profile": profile1, "store": store, "jobName": jobName, "jobTimestamp": timestampP]
)

// visit the Development environment
String profile2 = "Flaskr_DevelopmentEnv"
profilesLoader.loadProfile(profile2)
WebUI.comment("Execution Profile ${profile2} was loaded with GlobalVariable.URL=${GlobalVariable.URL}")
JobTimestamp timestampD = JobTimestamp.laterThan(timestampP)

WebUI.callTestCase(
	findTestCase("main/Flaskr/materialize"),
	["profile": profile2, "store": store, "jobName": jobName, "jobTimestamp": timestampD]
)



// --------------------------------------------------------------------
/*
 * Reduce stage
 */
// identify 2 MaterialList objects: left and right = production and development
// compare the right(development) with the left(production)
// find differences between the 2 versions. --- Twins mode

MaterialList left = store.select(jobName, timestampP,
	QueryOnMetadata.builder([ "profile": profile1 ]).build())

MaterialList right = store.select(jobName, timestampD,
	QueryOnMetadata.builder([ "profile": profile2 ]).build())

SortKeys sortKeys = new SortKeys("step", "URL.path")

MaterialProductGroup reduced =
    WebUI.callTestCase(findTestCase("main/Flaskr/reduceTwins"),
		["store": store,
			"leftMaterialList": left,
			"rightMaterialList": right,
			"sortKeys": sortKeys
			])


//---------------------------------------------------------------------
/*
 * Report stage
 */
// compile a human-readable report
int warnings =
	WebUI.callTestCase(findTestCase("main/Flaskr/report"),
		["store": store, "mProductGroup": reduced, "sortKeys": sortKeys,"criteria": 2.0d])


//---------------------------------------------------------------------
/*
 * Epilogue
 */
if (warnings > 0) {
	KeywordUtil.markWarning("found ${warnings} differences.")
}
