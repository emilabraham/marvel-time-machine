package com.emilabraham.marveltimemachine

/**
 * The data attribute from MarvelApiResponse. Contains the list of comics.
 */

class ComicsData(val limit: Int, val total: Int, val count: Int, val results: List<Comic>)