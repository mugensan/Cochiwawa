import { gql } from "apollo-server-express";

export const typeDefs = gql`
  type User {
    id: ID!
    email: String!
    role: String!
    fullName: String
    gender: String
    nationalId: String
    profilePhotoUrl: String
  }

  type AuthResponse {
    token: String!
    user: User
  }

  type Ride {
    id: ID!
    driverId: String!
    origin: String!
    destination: String!
    departureTime: String!
    availableSeats: Int!
    pricePerSeat: Float!
  }

  type Booking {
    id: ID!
    passengerId: String!
    rideId: Int!
    seats: Int!
    createdAt: String!
    ride: Ride
  }

  type Earnings {
    totalEarnings: Float
    totalRides: Int
  }

  type Query {
    me: User
    getDriverRides(driverId: String!): [Ride]!
    getPassengerBookings(passengerId: String!): [Booking]!
    getDriverEarnings(driverId: String!): Earnings!
  }

  type Mutation {
    signUp(
      email: String!,
      password: String!,
      fullName: String!,
      gender: String!,
      nationalId: String!,
      role: String!,
      profilePhotoUrl: String!
    ): AuthResponse!

    signIn(email: String!, password: String!): AuthResponse!

    registerVehicle(
      driverId: String!,
      driverLicenseNumber: String!,
      vehiclePlate: String!,
      vehicleModel: String!,
      vehicleColor: String!,
      insuranceDocumentUrl: String!,
      carPhotoFront: String!,
      carPhotoBack: String!,
      carPhotoLeft: String!,
      carPhotoDriverSeat: String!
    ): Boolean!

    triggerEmergencyAlert(userId: String!, lat: Float!, lng: Float!): Boolean!

    bookRide(rideId: Int!, seats: Int!): Booking!
  }
`;
