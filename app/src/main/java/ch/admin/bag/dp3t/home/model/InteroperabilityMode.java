/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package ch.admin.bag.dp3t.home.model;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import ch.admin.bag.dp3t.R;

public enum InteroperabilityMode {
    LEGACY,
    DISABLED,
    COUNTRIES,
    COUNTRIES_UPDATE_PENDING,
    EU;

    public static int toInt(InteroperabilityMode interoperabilityMode) {
        if (interoperabilityMode == null) {
            return -1;
        }

        switch (interoperabilityMode) {
            case LEGACY:
                return 0;
            case DISABLED:
                return 1;
            case COUNTRIES:
                return 2;
            case COUNTRIES_UPDATE_PENDING:
                return 3;
            case EU:
                return 4;
            default:
                return -1;
        }
    }

    @Nullable
    public static InteroperabilityMode getFromInt(int interoperabilityMode) {
        switch (interoperabilityMode) {
            case 0:
                return LEGACY;
            case 1:
                return DISABLED;
            case 2:
                return COUNTRIES;
            case 3:
                return COUNTRIES_UPDATE_PENDING;
            case 4:
                return EU;
            default:
                return null;
        }
    }

    @StringRes
    public static int getTitle(InteroperabilityMode interoperabilityMode) {
        switch (interoperabilityMode) {
            case LEGACY:
                return R.string.interop_mode_legacy_title;
            case DISABLED:
                return R.string.interop_mode_disabled_title;
            case COUNTRIES:
            case COUNTRIES_UPDATE_PENDING:
                return R.string.interop_mode_countries_title;
            case EU:
                return R.string.interop_mode_eu_title;
            default:
                return -1;
        }
    }

    @StringRes
    public static int getText(InteroperabilityMode interoperabilityMode) {
        switch (interoperabilityMode) {
            case LEGACY:
                return R.string.interop_mode_legacy_text_alt;
            case DISABLED:
                return R.string.interop_mode_disabled_text_alt;
            case COUNTRIES:
                return R.string.interop_mode_countries_text_alt;
            case COUNTRIES_UPDATE_PENDING:
                return R.string.interop_mode_countries_update_pending_text;
            case EU:
                return R.string.interop_mode_eu_text_alt;
            default:
                return -1;
        }
    }

    @DrawableRes
    public static int getIcon(InteroperabilityMode interoperabilityMode) {
        switch (interoperabilityMode) {
            case LEGACY:
            case COUNTRIES_UPDATE_PENDING:
                return R.drawable.ic_warning;
            case COUNTRIES:
            case EU:
                return R.drawable.ic_check;
            case DISABLED:
            default:
                return R.drawable.ic_info;
        }
    }

    @ColorRes
    public static int getTitleTextColor(InteroperabilityMode interoperabilityMode) {
        switch (interoperabilityMode) {
            case COUNTRIES:
            case EU:
            case DISABLED:
                return R.color.white;
            case LEGACY:
            case COUNTRIES_UPDATE_PENDING:
            default:
                return R.color.dark_main;
        }
    }

    @ColorRes
    public static int getTextColor(InteroperabilityMode interoperabilityMode) {
        switch (interoperabilityMode) {
            case COUNTRIES:
            case DISABLED:
            case EU:
                return R.color.white;
            case LEGACY:
            case COUNTRIES_UPDATE_PENDING:
            default:
                return R.color.dark_main;
        }
    }

    @ColorRes
    public static int getBackgroundColor(InteroperabilityMode interoperabilityMode) {
        switch (interoperabilityMode) {
            case LEGACY:
            case COUNTRIES_UPDATE_PENDING:
                return R.color.yellow_main;
            case COUNTRIES:
            case EU:
                return R.color.purple_main;
            case DISABLED:
            default:
                return R.color.blue_main;
        }
    }
}
