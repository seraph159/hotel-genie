# Base stage for dependencies and build
FROM node:18-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json for dependency installation
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy the entire application code to the working directory
COPY . .

# Build the Next.js application
RUN npm run build

# Final stage for production
FROM node:18-alpine AS production

# Set the working directory for the production environment
WORKDIR /app

# Copy the built application and necessary files from the builder stage
COPY --from=builder /app ./

# Install only production dependencies
RUN npm ci --only=production

# Expose the port the application will run on
EXPOSE 3000

# Start the application
CMD ["npm", "start"]