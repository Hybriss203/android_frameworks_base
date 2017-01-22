/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.content.pm;

import android.annotation.IntRange;
import android.annotation.NonNull;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.List;

/**
 * This class provides information for a shared library. There are
 * three types of shared libraries: builtin - non-updatable part of
 * the OS; dynamic - updatable backwards-compatible dynamically linked;
 * static - updatable non backwards-compatible emulating static linking.
 */
public final class SharedLibraryInfo implements Parcelable {
    /**
     * Shared library type: this library is a part of the OS
     * and cannot be updated or uninstalled.
     * @hide
     */
    public static final int TYPE_BUILTIN = 0x1<<0;

    /**
     * Shared library type: this library is backwards-compatible, can
     * be updated, and updates can be uninstalled. Clients link against
     * the latest version of the library.
     * @hide
     */
    public static final int TYPE_DYNAMIC = 0x1<<1;

    /**
     * Shared library type: this library is <strong>not</strong> backwards
     * -compatible, can be updated and updates can be uninstalled. Clients
     * link against a specific version of the library.
     * @hide
     */
    public static final int TYPE_STATIC = 0x1<<2;

    /**
     * Constant for referring to an undefined version.
     */
    public static final int VERSION_UNDEFINED = -1;

    private final String mName;
    private final int mVersion;
    private final int mType;
    private final VersionedPackage mDeclaringPackage;
    private final List<VersionedPackage> mDependentPackages;

    /**
     * Creates a new instance.
     *
     * @param name The lib name.
     * @param version The lib version if not builtin.
     * @param type The lib type.
     * @param declaringPackage The package that declares the library.
     * @param dependentPackages The packages that depend on the library.
     *
     * @hide
     */
    public SharedLibraryInfo(String name, int version, int type,
            VersionedPackage declaringPackage, List<VersionedPackage> dependentPackages) {
        mName = name;
        mVersion = version;
        mType = type;
        mDeclaringPackage = declaringPackage;
        mDependentPackages = dependentPackages;
    }

    private SharedLibraryInfo(Parcel parcel) {
        this(parcel.readString(), parcel.readInt(), parcel.readInt(),
                parcel.readParcelable(null), parcel.readArrayList(null));
    }

    /** @hide */
    public int getType() {
        return mType;
    }

    /**
     * Gets the library name.
     *
     * @return The name.
     */
    public String getName() {
        return mName;
    }

    /**
     * Gets the version of the library. For {@link #isStatic()}  static} libraries
     * this is the declared version and for {@link #isDynamic()} dynamic} and
     * {@link #isBuiltin()} builtin} it is {@link #VERSION_UNDEFINED} as these
     * are not versioned.
     *
     * @return The version.
     */
    public @IntRange(from = -1) int getVersion() {
        return mVersion;
    }

    /**
     * @return whether this library is builtin which means that it
     * is a part of the OS and cannot be updated or uninstalled.
     */
    public boolean isBuiltin() {
        return mType == TYPE_BUILTIN;
    }

    /**
     * @return whether this library is dynamic which means that it
     * is backwards-compatible, can be updated, and updates can be
     * uninstalled. Clients link against the latest version of the
     * library.
     */
    public boolean isDynamic() {
        return mType == TYPE_DYNAMIC;
    }

    /**
     * @return whether this library is dynamic which means that it
     * is <strong>not</strong> backwards-compatible, can be updated
     * and updates can be uninstalled. Clients link against a specific
     * version of the library.
     */
    public boolean isStatic() {
        return mType == TYPE_STATIC;
    }

    /**
     * Gets the package that declares the library.
     *
     * @return The package declaring the library.
     */
    public @NonNull VersionedPackage getDeclaringPackage() {
        return mDeclaringPackage;
    }

    /**
     * Gets the packages that depend on the library.
     *
     * @return The dependent packages.
     */
    public @NonNull List<VersionedPackage> getDependentPackages() {
        if (mDependentPackages == null) {
            return Collections.emptyList();
        }
        return mDependentPackages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "SharedLibraryInfo[name:" + mName + ", type:" + typeToString(mType)
                + ", version:" + mVersion + (!getDependentPackages().isEmpty()
                ? " has dependents" : "");
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mName);
        parcel.writeInt(mVersion);
        parcel.writeInt(mType);
        parcel.writeParcelable(mDeclaringPackage, flags);
        parcel.writeList(mDependentPackages);
    }

    private static String typeToString(int type) {
        switch (type) {
            case TYPE_BUILTIN: {
                return "builtin";
            }
            case TYPE_DYNAMIC: {
                return "dynamic";
            }
            case TYPE_STATIC: {
                return "static";
            }
            default: {
                return "unknown";
            }
        }
    }

    public static final Parcelable.Creator<SharedLibraryInfo> CREATOR =
            new Parcelable.Creator<SharedLibraryInfo>() {
        public SharedLibraryInfo createFromParcel(Parcel source) {
            return new SharedLibraryInfo(source);
        }

        public SharedLibraryInfo[] newArray(int size) {
            return new SharedLibraryInfo[size];
        }
    };
}
