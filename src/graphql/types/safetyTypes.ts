/**
 * GraphQL Type Definitions for Identity Verification System
 */

export const identityTypeDefs = `
  enum Gender {
    MALE
    FEMALE
    OTHER
  }

  enum IdentityDocumentType {
    RUT
    PASSPORT
    NATIONAL_ID
  }

  type UserIdentity {
    id: Int!
    fullName: String
    gender: Gender
    rut: String
    passport: String
    nationalId: String
    profilePhotoUrl: String
    facialVerification: Boolean!
    verified: Boolean!
    createdAt: String
    updatedAt: String
  }

  type VerificationResult {
    success: Boolean!
    message: String!
    userIdentity: UserIdentity
  }

  extend type Mutation {
    updateUserIdentity(
      userId: Int!
      userType: String!
      fullName: String!
      gender: Gender!
      rut: String
      passport: String
      nationalId: String
      profilePhotoUrl: String!
    ): VerificationResult!

    verifyDocument(
      documentType: IdentityDocumentType!
      documentValue: String!
    ): Boolean!
  }
`;

/**
 * GraphQL Type Definitions for Safety Features
 */

export const safetyTypeDefs = `
  enum GenderPreference {
    ANY
    FEMALE_ONLY
  }

  type EmergencyAlert {
    id: String!
    userId: Int!
    userType: String!
    latitude: Float!
    longitude: Float!
    status: String!
    createdAt: String!
  }

  type EmergencyResponse {
    success: Boolean!
    alertId: String
    message: String!
  }

  type TermsAcceptance {
    id: String!
    userId: Int!
    userType: String!
    acceptedAt: String!
    termsVersion: String!
  }

  extend type Mutation {
    triggerEmergencyAlert(
      userId: Int!
      userType: String!
      latitude: Float!
      longitude: Float!
    ): EmergencyResponse!

    dismissEmergencyAlert(alertId: String!): Boolean!

    acceptTerms(userId: Int!, userType: String!): Boolean!
  }

  extend type Query {
    getEmergencyAlerts: [EmergencyAlert!]!
    getUserEmergencyAlerts(userId: Int!, userType: String!): [EmergencyAlert!]!
  }
`;

/**
 * GraphQL Type Definitions for Vehicle Verification
 */

export const vehicleTypeDefs = `
  type DriverVehicle {
    id: String!
    driverId: Int!
    driverLicenseNumber: String!
    vehiclePlate: String!
    vehicleModel: String!
    vehicleColor: String!
    insuranceDocumentUrl: String
    carPhotoFront: String
    carPhotoBack: String
    carPhotoLeft: String
    carPhotoDriverSeat: String
    verified: Boolean!
    createdAt: String!
    updatedAt: String!
  }

  enum AdminReviewStatus {
    PENDING
    APPROVED
    REJECTED
  }

  type AdminReview {
    id: String!
    driverId: Int!
    vehicleId: String
    reviewType: String!
    status: AdminReviewStatus!
    notes: String
    reviewedBy: Int
    createdAt: String!
    updatedAt: String!
  }

  type VehicleRegistrationResult {
    success: Boolean!
    vehicleId: String
    message: String!
  }

  extend type Mutation {
    registerVehicle(
      driverId: Int!
      driverLicenseNumber: String!
      vehiclePlate: String!
      vehicleModel: String!
      vehicleColor: String!
      insuranceDocumentUrl: String
      carPhotoFront: String
      carPhotoBack: String
      carPhotoLeft: String
      carPhotoDriverSeat: String
    ): VehicleRegistrationResult!

    updateVehicleImages(
      vehicleId: String!
      carPhotoFront: String
      carPhotoBack: String
      carPhotoLeft: String
      carPhotoDriverSeat: String
    ): Boolean!

    verifyVehicle(vehicleId: String!, notes: String): Boolean!

    approveVehicleReview(
      reviewId: String!
      notes: String
    ): Boolean!

    rejectVehicleReview(
      reviewId: String!
      notes: String!
    ): Boolean!
  }

  extend type Query {
    getDriverVehicles(driverId: Int!): [DriverVehicle!]!
    getVehicleDetails(vehicleId: String!): DriverVehicle
    getAllUnverifiedVehicles(limit: Int, offset: Int): [DriverVehicle!]!
    getAdminReviews(driverId: Int): [AdminReview!]!
  }
`;

/**
 * GraphQL Type Definitions for Ride Extensions
 */

export const rideExtensionTypeDefs = `
  type RideExtended {
    id: Int!
    driverId: Int!
    origin: String!
    destination: String!
    departureTime: String!
    availableSeats: Int!
    pricePerSeat: Float!
    genderPreference: GenderPreference!
    createdAt: String!
    updatedAt: String!
  }

  type MatchingResult {
    rideId: Int!
    distance: Float!
    matchScore: Float!
    driver: DriverProfile
  }

  type DriverProfile {
    id: Int!
    firstName: String!
    lastName: String!
    email: String!
    verified: Boolean!
    vehicle: DriverVehicle
  }

  extend type Mutation {
    createRideWithPreferences(
      driverId: Int!
      origin: String!
      destination: String!
      departureTime: String!
      availableSeats: Int!
      pricePerSeat: Float!
      genderPreference: GenderPreference
    ): RideExtended!
  }

  extend type Query {
    findMatchingRides(
      passengerId: Int!
      lat: Float!
      lng: Float!
      maxDistance: Float
    ): [MatchingResult!]!
  }
`;

/**
 * Complete schema for safety and verification system
 */
export const safetySystemTypeDefs = [
    identityTypeDefs,
    safetyTypeDefs,
    vehicleTypeDefs,
    rideExtensionTypeDefs
];

export default safetySystemTypeDefs;
