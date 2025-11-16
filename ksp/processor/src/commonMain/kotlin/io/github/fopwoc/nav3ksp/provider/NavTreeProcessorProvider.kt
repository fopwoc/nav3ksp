package io.github.fopwoc.nav3ksp.provider

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import io.github.fopwoc.nav3ksp.processor.NavTreeProcessor

class NavTreeProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return NavTreeProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
        )
    }
}
