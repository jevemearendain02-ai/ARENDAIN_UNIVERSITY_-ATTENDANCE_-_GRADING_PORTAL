package com.smartlostfound.model;

/**
 * Every role that can register in the system.
 * Keep this in sync with Section 2 of the approved System Proposal.
 * The database CHECK constraint and the registration menu are both generated from this enum.
 */
public enum Role {
    ADMIN,
    TEACHER,
    STUDENT,
    STAFF,
    SECURITY
}
