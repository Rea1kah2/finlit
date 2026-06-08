package com.finlit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FinlitApplication

fun main(args: Array<String>) {
	runApplication<FinlitApplication>(*args)
}
