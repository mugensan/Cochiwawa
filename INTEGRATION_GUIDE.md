## Cochiwawa Backend - Safety & Verification System Integration Guide

This guide covers the integration of the new safety verification system into the Cochiwawa backend.

### Features Added

1. **User Identity Verification**
   - Support for Chilean RUT, Passport, and National ID
   - Profile photo management
   - Gender selection (MALE, FEMALE, OTHER)
   - Facial verification flag

2. **Driver Vehicle Verification**
   - Driver license number tracking
   - Vehicle registration details
   - Multiple vehicle photo uploads (front, back, side, interior)
   - Insurance document storage
   - Admin approval workflow

3. **Gender-Based Ride Preferences**
   - Drivers can set gender preferences (ANY, FEMALE_ONLY)
   - Safety matching algorithm enforces preferences
   - Passengers must match preferences to be assigned

4. **Emergency Alert System**
   - Location-based emergency reporting
   - Active alerts tracking
   - WebSocket broadcast capability
   - Admin dashboard integration

5. **Legal Compliance**
   - Terms acceptance tracking
   - Version management
   - Per-user audit trail

### Database Migrations

New migrations have been created to support the system:

- **006_update_users_identity.sql** - Adds identity fields to drivers and passengers
- **007_add_ride_preferences.sql** - Adds gender preference to rides
- **008_create_emergency_alerts.sql** - Creates emergency alerts table
- **009_terms_acceptance.sql** - Creates terms acceptance table
- **010_create_driver_vehicles.sql** - Creates driver vehicles table
- **011_admin_reviews.sql** - Creates admin review workflows

**To run migrations:**
```bash
npm run build
npx ts-node src/migration/runMigrations.ts
```

### Directory Structure

```
src/
├── services/
│   ├── identityService.ts          # Identity verification logic
│   ├── emergencyService.ts         # Emergency alert handling
│   ├── vehicleService.ts           # Vehicle verification
│   └── matchingService.ts          # Safety-aware ride matching
├── graphql/
│   ├── types/
│   │   ├── safetyTypes.ts          # GraphQL type definitions
│   │   └── index.ts
│   ├── resolvers/
│   │   ├── safetyResolvers.ts      # GraphQL resolvers
│   │   └── index.ts
│   ├── schema.ts                   # Schema combination
│   └── index.ts
├── migration/
│   ├── 006_update_users_identity.sql
│   ├── 007_add_ride_preferences.sql
│   ├── 008_create_emergency_alerts.sql
│   ├── 009_terms_acceptance.sql
│   ├── 010_create_driver_vehicles.sql
│   └── 011_admin_reviews.sql
└── index.ts                        # Main Express app
```

### API Endpoints

#### Identity Management
- `POST /api/identity/update` - Update user identity
- `GET /api/identity/:userId/:userType` - Get user identity info

#### Emergency Alerts
- `POST /api/emergency/alert` - Create emergency alert
- `GET /api/emergency/alerts/active` - Get all active alerts
- `GET /api/emergency/alerts/:userId/:userType` - Get user's alerts
- `PATCH /api/emergency/alert/:alertId/resolve` - Resolve alert

#### Vehicle Management
- `POST /api/vehicle/register` - Register driver vehicle
- `GET /api/vehicle/:vehicleId` - Get vehicle details
- `GET /api/vehicle/driver/:driverId` - Get driver's vehicles
- `PATCH /api/vehicle/:vehicleId/verify` - Verify vehicle (admin)

#### Ride Matching
- `POST /api/rides/find-matches` - Find matching rides for passenger
- `POST /api/rides/validate-passenger` - Validate passenger for ride
- `GET /api/rides/driver/:driverId/can-create` - Check driver eligibility

### GraphQL Mutations and Queries

**Mutations:**
```graphql
mutation UpdateIdentity {
  updateUserIdentity(
    userId: Int!
    userType: String!
    fullName: String!
    gender: Gender!
    rut: String
    passport: String
    nationalId: String
    profilePhotoUrl: String!
  )
}

mutation TriggerEmergency {
  triggerEmergencyAlert(
    userId: Int!
    userType: String!
    latitude: Float!
    longitude: Float!
  )
}

mutation RegisterVehicle {
  registerVehicle(
    driverId: Int!
    driverLicenseNumber: String!
    vehiclePlate: String!
    vehicleModel: String!
    vehicleColor: String!
    carPhotoFront: String!
    carPhotoBack: String!
    carPhotoLeft: String!
    carPhotoDriverSeat: String!
  )
}

mutation CreateRide {
  createRideWithPreferences(
    driverId: Int!
    origin: String!
    destination: String!
    departureTime: String!
    availableSeats: Int!
    pricePerSeat: Float!
    genderPreference: GenderPreference
  )
}
```

**Queries:**
```graphql
query GetRideMatches {
  findMatchingRides(
    passengerId: Int!
    lat: Float!
    lng: Float!
    maxDistance: Float
  )
}

query GetEmergencyAlerts {
  getEmergencyAlerts
}

query GetVehicles {
  getDriverVehicles(driverId: Int!)
}

query GetReviews {
  getAdminReviews(driverId: Int)
}
```

### Implementation for Cloud Storage

The services include placeholder functions for uploading images to cloud storage:
- `uploadProfilePhoto()` - Profile photo upload
- `uploadVehicleImage()` - Vehicle image upload

To implement actual cloud storage:

**Option 1: AWS S3**
```typescript
import AWS from 'aws-sdk';
const s3 = new AWS.S3();

export async function uploadProfilePhoto(file: any): Promise<string> {
    const params = {
        Bucket: process.env.S3_BUCKET,
        Key: `profile-photos/${Date.now()}-${file.originalname}`,
        Body: file.buffer
    };
    const result = await s3.upload(params).promise();
    return result.Location;
}
```

**Option 2: Cloudflare R2**
```typescript
import AWS from 'aws-sdk';
const s3 = new AWS.S3({
    endpoint: process.env.R2_ENDPOINT,
    accessKeyId: process.env.R2_ACCESS_KEY,
    secretAccessKey: process.env.R2_SECRET_KEY,
    s3ForcePathStyle: true,
    signatureVersion: 'v4'
});
```

### Facial Recognition Integration

The `verifyFaceMatch()` function is a placeholder for facial recognition:

**Option 1: AWS Rekognition**
```typescript
import AWS from 'aws-sdk';
const rekognition = new AWS.Rekognition();

export async function verifyFaceMatch(photoUrl1: string, photoUrl2: string): Promise<boolean> {
    const result = await rekognition.compareFaces({
        SourceImage: { S3Object: { Bucket: '...', Name: '...' } },
        TargetImage: { S3Object: { Bucket: '...', Name: '...' } }
    }).promise();
    
    return result.FaceMatches && result.FaceMatches.length > 0;
}
```

**Option 2: Google Vision API**
```typescript
import vision from '@google-cloud/vision';

const client = new vision.ImageAnnotatorClient();

export async function verifyFaceMatch(photoUrl1: string, photoUrl2: string): Promise<boolean> {
    const request1 = { image: { source: { imageUri: photoUrl1 } } };
    const request2 = { image: { source: { imageUri: photoUrl2 } } };
    
    const [detections1] = await client.faceDetection(request1);
    const [detections2] = await client.faceDetection(request2);
    
    // Compare face data
    return detections1.faceAnnotations?.length > 0 && 
           detections2.faceAnnotations?.length > 0;
}
```

### Security Considerations

1. **Verification Enforcement**
   - Drivers must have `verified = true` AND `driver_vehicles.verified = true` to create rides
   - Passengers must have `verified = true` to book rides
   - Gender preferences are enforced before distance matching

2. **Gender Preferences**
   - `FEMALE_ONLY` rides only accept female passengers
   - `ANY` rides accept all verified passengers
   - Cannot be circumvented by matching algorithm

3. **Admin Review Process**
   - All new driver vehicles go to `admin_reviews` table with status `PENDING`
   - Admin must explicitly approve before vehicle is `verified = true`
   - Notes can be added for rejections

4. **Emergency Alerts**
   - Automatically broadcast via WebSocket
   - Stored with timestamp and location
   - Can be resolved or dismissed

### Testing the System

**1. Create a verified driver:**
```bash
curl -X POST http://localhost:5000/api/identity/update \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "userType": "driver",
    "fullName": "Juan Pérez",
    "gender": "MALE",
    "rut": "12.345.678-9",
    "profilePhotoUrl": "https://example.com/photo.jpg"
  }'
```

**2. Register driver vehicle:**
```bash
curl -X POST http://localhost:5000/api/vehicle/register \
  -H "Content-Type: application/json" \
  -d '{
    "driverId": 1,
    "driverLicenseNumber": "DL123456",
    "vehiclePlate": "ABC-1234",
    "vehicleModel": "Toyota Corolla",
    "vehicleColor": "Silver",
    "carPhotoFront": "https://example.com/front.jpg",
    "carPhotoBack": "https://example.com/back.jpg",
    "carPhotoLeft": "https://example.com/left.jpg",
    "carPhotoDriverSeat": "https://example.com/interior.jpg"
  }'
```

**3. Verify vehicle (admin):**
```bash
curl -X PATCH http://localhost:5000/api/vehicle/{vehicleId}/verify
```

**4. Check if driver can create rides:**
```bash
curl http://localhost:5000/api/rides/driver/1/can-create
```

### Environment Variables

```env
DATABASE_URL=postgresql://user:password@localhost:5432/cochiwawa
PORT=5000

# Optional: Cloud Storage
S3_BUCKET=cochiwawa-storage
S3_REGION=us-east-1
AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key

# Optional: Facial Recognition
AWS_REKOGNITION_REGION=us-east-1
GOOGLE_VISION_API_KEY=your_api_key
```

### Next Steps

1. **Apollo Server Integration** - Set up Apollo Server for full GraphQL support
2. **WebSocket Integration** - Implement Socket.io for real-time emergency alerts
3. **Email Notifications** - Add email alerts for emergency situations
4. **SMS Integration** - Send SMS notifications to emergency contacts
5. **Admin Dashboard** - Build dashboard for vehicle verification and alert management
6. **Mobile App** - Develop iOS/Android apps with emergency button and GPS tracking

### Troubleshooting

**Issue: Migrations not running**
- Ensure PostgreSQL is running
- Check DATABASE_URL environment variable
- Run: `npm run build && npx ts-node src/migration/runMigrations.ts`

**Issue: Services throwing errors**
- Check database connection
- Verify tables exist: `\dt` in psql
- Check service imports in index.ts

**Issue: GraphQL types not recognized**
- Ensure GraphQL server is properly configured
- Check type definitions in safetyTypes.ts
- Verify resolvers are exported correctly

