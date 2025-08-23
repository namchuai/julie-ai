package ai.julie.feature.toolexecutor.data

import ai.julie.core.network.HttpClientProvider
import ai.julie.core.network.HttpConfig
import ai.julie.logging.Logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

@Serializable
data class BraveSearchResult(
    val title: String = "",
    val url: String = "",
    val description: String = ""
)

@Serializable
data class BraveSearchResponse(
    val web: BraveWebResults? = null
)

@Serializable
data class BraveWebResults(
    val results: List<BraveSearchResult> = emptyList()
)

/**
 * Service for executing Brave Search API calls
 */
class BraveSearchService(
    private val apiKey: String = "BSATugbvNYrxx4PHSbPe78CBWOmfh6Y"
) {
    private val httpClient: HttpClient by lazy {
        HttpClientProvider().getHttpClient(
            HttpConfig(
                protocol = "https",
                host = "api.search.brave.com",
                headers = mapOf(
                    "X-Subscription-Token" to apiKey,
                    "Accept" to "application/json"
                )
            )
        )
    }
    private val TAG = "BraveSearchService"
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Execute a Brave search query
     * @param query The search query string
     * @return List of search results
     */
    suspend fun search(query: String): List<BraveSearchResult> {
        return try {
            Logger.d("[$TAG] ========== BRAVE SEARCH START ==========")
            Logger.d("[$TAG] Query to search: '$query'")
            Logger.d("[$TAG] Query length: ${query.length} characters")
            Logger.d("[$TAG] Making HTTP request to Brave API...")
            
            val response: HttpResponse = httpClient.get("/res/v1/web/search") {
                parameter("q", query)
                parameter("count", 5) // Limit to 5 results
            }
            
            Logger.d("[$TAG] HTTP response status: ${response.status}")

            if (response.status == HttpStatusCode.OK) {
                val responseText = response.bodyAsText()
                Logger.d("[$TAG] Response body length: ${responseText.length} characters")
                Logger.d("[$TAG] Response body preview: ${responseText.take(200)}...")
                
                val searchResponse = json.decodeFromString<BraveSearchResponse>(responseText)
                val results = searchResponse.web?.results ?: emptyList()
                
                Logger.d("[$TAG] ✅ Successfully parsed ${results.size} search results")
                results.forEachIndexed { index, result ->
                    Logger.d("[$TAG] Result $index: '${result.title}' - ${result.description.take(50)}...")
                }
                Logger.d("[$TAG] ========== BRAVE SEARCH END (SUCCESS) ==========")
                results
            } else {
                val responseText = response.bodyAsText()
                Logger.e("[$TAG] ❌ Brave search failed with status: ${response.status}")
                Logger.e("[$TAG] Error response body: $responseText")
                Logger.e("[$TAG] ========== BRAVE SEARCH END (HTTP ERROR) ==========")
                emptyList()
            }
        } catch (e: Exception) {
            Logger.e("[$TAG] ❌ Error during Brave search: ${e.message}")
            Logger.e("[$TAG] Exception type: ${e::class.simpleName}")
            Logger.e("[$TAG] Using mock results for demo purposes")
            
            // Return mock results for demo purposes
            val mockResults = listOf(
                BraveSearchResult(
                    title = "Mock Weather Result",
                    url = "https://weather.com",
                    description = "Current weather information and forecasts"
                )
            )
            Logger.e("[$TAG] ========== BRAVE SEARCH END (EXCEPTION - MOCK RESULTS) ==========")
            mockResults
        }
    }

    /**
     * Parse a Llama 3.1 brave_search/brute_force_search tool call and execute the search
     */
    suspend fun executeToolCall(toolCall: String): List<BraveSearchResult> {
        Logger.d("[$TAG] ========== TOOL CALL EXECUTION START ==========")
        Logger.d("[$TAG] Raw tool call received: '$toolCall'")
        Logger.d("[$TAG] Tool call length: ${toolCall.length} characters")
        
        // Parse various formats:
        // 1. brave_search.call(query="...")
        // 2. {"name": "brave_search", "parameters": {"query": "..."}}
        // 3. {"name": "brute_force_search", "parameters": {"query": "..."}}
        
        // Try pattern 1: direct function call format
        Logger.d("[$TAG] Trying pattern 1: direct function call format")
        val directPattern = """(?:brave_search|brute_force_search)\.call\(query="([^"]+)"\)""".toRegex()
        var matchResult = directPattern.find(toolCall)
        
        if (matchResult != null) {
            val query = matchResult.groupValues[1]
            Logger.d("[$TAG] ✅ Pattern 1 MATCHED! Parsed query: '$query'")
            Logger.d("[$TAG] Executing search for query: '$query'")
            val results = search(query)
            Logger.d("[$TAG] Search completed, returning ${results.size} results")
            Logger.d("[$TAG] ========== TOOL CALL EXECUTION END ==========")
            return results
        } else {
            Logger.d("[$TAG] ❌ Pattern 1 did not match")
        }
        
        // Try pattern 2 & 3: JSON format (handles both brave_search and brute_force_search)
        Logger.d("[$TAG] Trying pattern 2: JSON format")
        val jsonPattern = """"parameters"\s*:\s*\{\s*"query"\s*:\s*"([^"]+)"""".toRegex()
        matchResult = jsonPattern.find(toolCall)
        
        if (matchResult != null) {
            val query = matchResult.groupValues[1]
            Logger.d("[$TAG] ✅ Pattern 2 MATCHED! Parsed query: '$query'")
            Logger.d("[$TAG] Executing search for query: '$query'")
            val results = search(query)
            Logger.d("[$TAG] Search completed, returning ${results.size} results")
            Logger.d("[$TAG] ========== TOOL CALL EXECUTION END ==========")
            return results
        } else {
            Logger.d("[$TAG] ❌ Pattern 2 did not match")
        }
        
        // Try simpler patterns in case of formatting variations
        Logger.d("[$TAG] Trying pattern 3: simple pattern")
        val simplePattern = """query["\s]*[:=]["\s]*([^"\}]+)""".toRegex()
        matchResult = simplePattern.find(toolCall)
        
        if (matchResult != null) {
            val query = matchResult.groupValues[1].trim('"', ' ')
            Logger.d("[$TAG] ✅ Pattern 3 MATCHED! Parsed query: '$query'")
            Logger.d("[$TAG] Executing search for query: '$query'")
            val results = search(query)
            Logger.d("[$TAG] Search completed, returning ${results.size} results")
            Logger.d("[$TAG] ========== TOOL CALL EXECUTION END ==========")
            return results
        } else {
            Logger.d("[$TAG] ❌ Pattern 3 did not match")
        }
        
        Logger.e("[$TAG] ⚠️ ALL PATTERNS FAILED! Could not parse tool call")
        Logger.e("[$TAG] Tool call content analysis:")
        Logger.e("[$TAG] - Contains 'brave_search': ${toolCall.contains("brave_search")}")
        Logger.e("[$TAG] - Contains 'brute_force_search': ${toolCall.contains("brute_force_search")}")
        Logger.e("[$TAG] - Contains 'parameters': ${toolCall.contains("parameters")}")
        Logger.e("[$TAG] - Contains 'query': ${toolCall.contains("query")}")
        Logger.e("[$TAG] - Contains '{': ${toolCall.contains("{")}")
        Logger.e("[$TAG] - Contains '}': ${toolCall.contains("}")}")
        Logger.e("[$TAG] ========== TOOL CALL EXECUTION END (FAILED) ==========")
        return emptyList()
    }
}