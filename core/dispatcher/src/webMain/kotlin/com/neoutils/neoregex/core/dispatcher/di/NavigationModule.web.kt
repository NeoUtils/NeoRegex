/*
 * NeoRegex.
 *
 * Copyright (C) 2024 Irineu A. Silva.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.neoutils.neoregex.core.dispatcher.di

import com.neoutils.neoregex.core.dispatcher.control.Controller
import com.neoutils.neoregex.core.dispatcher.control.ControllerImpl
import com.neoutils.neoregex.core.dispatcher.navigator.NavigationManager
import com.neoutils.neoregex.core.dispatcher.impl.NavigationManagerWeb
import org.koin.dsl.bind
import org.koin.dsl.module

actual val dispatcherModule = module {
    single { NavigationManagerWeb() } bind NavigationManager::class
    single { ControllerImpl() } bind Controller::class
}