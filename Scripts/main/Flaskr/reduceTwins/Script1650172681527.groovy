import java.util.function.BiFunction

import com.kazurayam.materialstore.inspector.Inspector
import com.kazurayam.materialstore.filesystem.MaterialList
import com.kazurayam.materialstore.reduce.MaterialProductGroup
import com.kazurayam.materialstore.reduce.Reducer
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

/**
 * Test Cases/main/Flaskr/reduce
 */
assert store != null
assert leftMaterialList != null
assert rightMaterialList != null
assert sortKeys != null

WebUI.comment("reduce started; store=${store}")
WebUI.comment("reduce started; leftMaterialList=${leftMaterialList}")
WebUI.comment("reduce started; rightMaterialList=${rightMaterialList}")
WebUI.comment("reduce started; sortKeys=${sortKeys}")

assert leftMaterialList.size() > 0
assert rightMaterialList.size() > 0

MaterialProductGroup reduced = 
        MaterialProductGroup.builder(leftMaterialList, rightMaterialList)
	        .ignoreKeys("profile", "URL.host", "URL.port", "URL.protocol")
	        .sort("step")
	        .build()
	
Inspector inspector = Inspector.newInstance(store)
inspector.setSortKeys(sortKeys)
MaterialProductGroup inspected = inspector.reduceAndSort(reduced)

return inspected


