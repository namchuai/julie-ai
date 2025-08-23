package ai.julie.feature.toolexecutor.domain

/**
 * Registry for managing available tools
 */
interface ToolRegistry {
    /**
     * Register a tool to make it available for execution
     * @param tool The tool to register
     */
    suspend fun register(tool: Tool)
    
    /**
     * Get a tool by name
     * @param name The name of the tool
     * @return The tool if found, null otherwise
     */
    suspend fun get(name: String): Tool?
    
    /**
     * List all registered tools
     * @return List of all available tools
     */
    suspend fun list(): List<Tool>
    
    /**
     * Unregister a tool
     * @param name The name of the tool to remove
     */
    suspend fun unregister(name: String)
}