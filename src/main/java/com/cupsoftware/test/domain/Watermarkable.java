package com.cupsoftware.test.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Watermarkable {

    /**
     * Since all properties are living inside the documents they easily be used as builders for Watermark objects.
     * @return a watermark.
     */
    @JsonIgnore
    Watermark createWatermark();
}
