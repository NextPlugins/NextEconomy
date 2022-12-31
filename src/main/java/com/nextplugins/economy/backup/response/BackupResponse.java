package com.nextplugins.economy.backup.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@Getter
@AllArgsConstructor
public final class BackupResponse {

    private final @Nullable File file;
    private final @NotNull ResponseType responseType;

}
