package com.hubtel.merchant.checkout.sdk.ux.shared

import androidx.navigation.NavHostController

/**
 * An abstract class that provides common feature navigation
 * functions.
 *
 * Its intended purpose is to be implemented by feature modules
 * and used to handle navigation operations of the feature module.
 *
 * @param navController used to govern the navigation of the feature module
 */
abstract class FeatureNavigator(val navController: NavHostController) {

    /**
     * Attempts to navigate up in the navigation hierarchy.
     *
     * @return true if navigation is successful and false otherwise.
     */
    fun back(): Boolean {
        return navController.popBackStack()
    }

}