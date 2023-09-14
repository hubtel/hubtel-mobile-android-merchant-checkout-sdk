package com.hubtel.merchant.checkout.sdk.ux.pay.status.incorrect_pin

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.layoutId
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hubtel.core_ui.components.custom.TealButton
import com.hubtel.core_ui.extensions.LocalActivity
import com.hubtel.core_ui.layouts.HBScaffold
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.R

internal data class WrongPINScreen(
    private val providerName: String?,
//    private val config: CheckoutConfig
) : Screen {
    @Composable
    override fun Content() {
//        val viewModel = viewModel<WrongPINScreenViewModel>(
//            factory = WrongPINScreenViewModel.getViewModelFactory(config.apiKey)
//        )

//        WrongPINScreenContent(wrongPINScreenViewModel = viewModel)
        WrongPINScreenContent()
    }

    @Composable
    private fun WrongPINScreenContent(/*wrongPINScreenViewModel: WrongPINScreenViewModel*/) {
        val screenHeight = LocalConfiguration.current.screenHeightDp
        val density = LocalDensity.current.density
        val maxHeight = (screenHeight / density).dp

        val context = LocalContext.current
        val activity = LocalActivity.current
        val navigator = LocalNavigator.current

        HBScaffold(backgroundColor = HubtelTheme.colors.uiBackground2, bottomBar = {
            Column(
                Modifier
                    .animateContentSize(),
            ) {
                Divider(
                    color = HubtelTheme.colors.outline,
                )
                TealButton(
                    onClick = {
//                              navigator?.push(
//                                  ConfirmOrderScreen("")
//                              )
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .padding(Dimens.paddingNano)
                ) {
                    Text(
                        text = stringResource(id = R.string.checkout_done).toUpperCase(),
                        style = HubtelTheme.typography.button,
                    )
                }
            }
        }) {
            val constraints = ConstraintSet {
                val backgroundBox = createRefFor("backgroundBox")
                val topBox = createRefFor("topBox")
                val buttonBox = createRefFor("buttonBox")

                constrain(backgroundBox) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }

                constrain(topBox) {
//                top.linkTo(backgroundBox.bottom, (-40).dp)
                    top.linkTo(backgroundBox.bottom, -Dimens.paddingExtraLarge)
                    start.linkTo(parent.start /* goneMargin = 15.dp*/)
                    end.linkTo(parent.end /* goneMargin = 15.dp*/)
                }

                constrain(buttonBox) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(topBox.bottom, Dimens.paddingLarge)
                }
            }

            ConstraintLayout(
                constraints, modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(maxHeight)
                        .background(color = Color(0xFFFFABBB))
                        .layoutId("backgroundBox")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.checkout_ic_close_circle_red),
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .padding(Dimens.paddingNano)
                        )
                        Text(
                            text = "Failed",
                            style = HubtelTheme.typography.h3,
                            modifier = Modifier.padding(bottom = Dimens.paddingDefault)
                        )

                        Text(
                            text = "You entered a wrong PIN",
                            style = HubtelTheme.typography.body1
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
//                    .height(120.dp)
                        .padding(Dimens.paddingDefault)
                        .background(Color(0xFFFFF4CC), shape = RoundedCornerShape(10.dp))
                        .layoutId("topBox")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(Dimens.paddingDefault)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.padding(Dimens.paddingNano)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.checkout_mtn_momo),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Text(
                            text = "MTN Mobile Wallet",
                            style = HubtelTheme.typography.body1,
                            modifier = Modifier.padding(
                                bottom = Dimens.paddingNano,
                                top = Dimens.paddingNano,
                            )
                        )
                        Text(text = "0241234567", style = HubtelTheme.typography.body1)
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(Dimens.paddingDefault)
//                    .background(color = Color.Blue)
                        .clickable {
                            // TODO: implement
                        }
                        .layoutId("buttonBox")
                ) {
                    Column {
                        Divider(
                            color = HubtelTheme.colors.outline,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Change Wallet", modifier = Modifier.padding(top = Dimens.paddingDefault, bottom = Dimens.paddingDefault).weight(2f))
                            Image(painter = painterResource(id = R.drawable.checkout_ic_forward_arrow), contentDescription = null,)
                        }
                        Divider(
                            color = HubtelTheme.colors.outline,
                        )
                    }
                }
            }
        }
    }
}