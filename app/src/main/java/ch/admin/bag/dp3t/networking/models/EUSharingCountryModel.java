/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.admin.bag.dp3t.networking.models;

public class EUSharingCountryModel {

    private String countryCode;
    private String countryNameEN;
    private String countryNameMT;

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryNameEN() {
        return countryNameEN;
    }

    public String getCountryNameMT() {
        return countryNameMT;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof EUSharingCountryModel)) {
            return false;
        }

        return this.getCountryCode().equals(((EUSharingCountryModel) other).getCountryCode());
    }
}
