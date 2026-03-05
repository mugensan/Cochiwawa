import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { UsersModule } from './users/users.module';
import { RidesModule } from './rides/rides.module';
import { BookingsModule } from './bookings/bookings.module';
import { PricingModule } from './pricing/pricing.module';

@Module({
  imports: [UsersModule, RidesModule, BookingsModule, PricingModule],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
